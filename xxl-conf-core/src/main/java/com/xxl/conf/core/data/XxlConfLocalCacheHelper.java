package com.xxl.conf.core.data;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.data.tool.ConfDataTool;
import com.xxl.conf.core.factory.XxlConfFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * local cache conf
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfLocalCacheHelper {
    private static Logger logger = LoggerFactory.getLogger(XxlConfHelper.class);

    // ---------------------- init ----------------------

    private XxlConfFactory xxlConfFactory;
    public XxlConfLocalCacheHelper(XxlConfFactory xxlConfFactory) {
        this.xxlConfFactory = xxlConfFactory;
    }


    // ---------------------- cache data ----------------------

    /**
     * <pre>
     *     // Data Structure
     *     "confData":{
     *          "app01":{
     *              "k1": "v1",
     *              "k2": "v2"
     *          }
     *     }
     *     "confDataMd5":{
     *          "app01":{
     *              "k1": md5(data),
     *              "k2": md5(data)
     *          }
     *     }
     * </pre>
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> confData = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> confDataMd5 = new ConcurrentHashMap<>();

    /**
     * make cache key
     *
     * @param appname
     * @param key
     * @return
     */
    public String buildCacheKey(String appname, String key){
        return String.format("%s##%s", appname, key);
    }

    // ---------------------- start / stop ----------------------


    private Thread refreshThread;
    private boolean toStop = false;

    /**
     * start
     */
    public void start(){
        // preload: TODO，1、本地文件，可用性；2、根据本地文件，批量预热，性能;3、非法数据过滤；

        // refresh thread
        refreshThread = startThread(new Runnable() {
            @Override
            public void run() {
                logger.info(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread start.");
                while (!toStop) {
                    try {
                        refreshData();
                    } catch (Throwable e) {
                        if (!toStop && !(e instanceof InterruptedException)) {
                            logger.error(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread stoped.");
            }
        }, "xxl-conf, XxlConfLocalCacheHelper-refreshThread");
    }

    /**
     * stop
     */
    public void stop(){
        // mark stop
        toStop = true;

        // do stop
        stopThread(refreshThread);
    }


    // ------------ tool

    /**
     * start thread
     *
     * @param runnable
     * @param name
     * @return
     */
    private static Thread startThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(name);
        thread.start();
        return thread;
    }

    /**
     * stop thread
     *
     * @param thread
     */
    private static void stopThread(Thread thread) {
        if (thread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            thread.interrupt();
            try {
                thread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    // ---------------------- util ----------------------

    /**
     * refresh Cache (Mirror not support), with real-time minitor
     */
    private void refreshData() throws InterruptedException{

        // valid
        if (confData.isEmpty()) {
            TimeUnit.SECONDS.sleep(3);
            return;
        }

        // monitor param
        Map<String, List<String>> confKey = new HashMap<>();
        for (Map.Entry<String, ConcurrentHashMap<String, String>> entry: confData.entrySet()) {
            String appname = entry.getKey();
            List<String> keyList = new ArrayList<>(entry.getValue().keySet());
            confKey.put(appname, keyList);
        }

        // monitor
        ConfDataTool.QueryConfDataResponse monitorRet = xxlConfFactory.getRemoteHelper().monitor(confKey, true);
        // 1、avoid fail-retry too quick；2、make sure server broadcast complete
        TimeUnit.SECONDS.sleep(1);

        // remote param
        confKey.clear();
        for (Map.Entry<String, ConcurrentHashMap<String, String>> entry: confData.entrySet()) {
            String appname = entry.getKey();
            List<String> keyList = new ArrayList<>(entry.getValue().keySet());
            confKey.put(appname, keyList);
        }
        // remote
        remoteQueryWithRefreshAndNotify(confKey);
        logger.debug(">>>>>>>>>> xxl-conf, refreshCacheAndMirror success.");
    }

    /**
     * remote query, with refresh and notify
     *
     * @param confKey
     */
    private void remoteQueryWithRefreshAndNotify(Map<String, List<String>> confKey){

        // remote query
        ConfDataTool.QueryConfDataResponse queryConfDataResponse = xxlConfFactory.getRemoteHelper().query(confKey, false);

        // parse response
        if (queryConfDataResponse.isSuccess() && queryConfDataResponse.getConfData()!=null && !queryConfDataResponse.getConfData().isEmpty())  {
            for (String appname: queryConfDataResponse.getConfData().keySet()) {

                // local-data of appname
                ConcurrentHashMap<String, String> confDataOfAppname = confData.computeIfAbsent(appname, k->new ConcurrentHashMap<>());
                ConcurrentHashMap<String, String> confDataMd5OfAppname = confDataMd5.computeIfAbsent(appname, k->new ConcurrentHashMap<>());

                // each key
                for (String key: queryConfDataResponse.getConfData().get(appname).keySet()) {
                    String value = queryConfDataResponse.getConfData().get(appname).get(key);
                    String valueMd5 = queryConfDataResponse.getConfDataMd5().containsKey(appname)
                            ? queryConfDataResponse.getConfDataMd5().get(appname).get(key)
                            :"";

                    // diff check
                    if (valueMd5.equals(confDataMd5OfAppname.get(key))) {
                        // filter repeat data
                        continue;
                    }

                    // refresh data
                    confDataOfAppname.put(key, value);
                    confDataMd5OfAppname.put(key, valueMd5);

                    // notify
                    xxlConfFactory.getListenerRepository().notifyChange(appname, key, value);
                }
            }
        }

        // support cache null value
        for (String appname: confKey.keySet()) {

            // local-data of appname
            ConcurrentHashMap<String, String> confDataOfAppname = confData.computeIfAbsent(appname, k->new ConcurrentHashMap<>());
            ConcurrentHashMap<String, String> confDataMd5OfAppname = confDataMd5.computeIfAbsent(appname, k->new ConcurrentHashMap<>());

            // each key
            for (String key: confKey.get(appname)) {
                if (!confDataOfAppname.containsKey(key)) {
                    confDataOfAppname.put(key, "");
                }
                if (!confDataMd5OfAppname.containsKey(key)) {
                    confDataMd5OfAppname.put(key, "");
                }
            }
        }

    }

    // ---------------------- tool ----------------------

    /**
     * get conf data
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public String get(String key, String defaultVal) {
        return get(xxlConfFactory.getAppname(), key, defaultVal);
    }

    /**
     * get conf data
     *
     * @param appname
     * @param key
     * @param defaultVal
     * @return
     */
    public String get(String appname, String key, String defaultVal) {

        // 1、get by cache
        if (confData.containsKey(appname) && confData.get(appname).containsKey(key)) {
            return confData.get(appname).get(key);
        }

        // 2、get from remote; get-and-watch
        Map<String, List<String>> confKey = new HashMap<>();
        confKey.put(appname, Arrays.asList(key));
        // remote
        try {
            remoteQueryWithRefreshAndNotify(confKey);
        } catch (Exception e) {
            logger.error(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper - get error, appname:{}, key:{}", appname, key, e);
        }

        // 2.1、new data from remote
        if (confData.containsKey(appname) && confData.get(appname).containsKey(key)) {
            return confData.get(appname).get(key);
        }

        // 3、default
        return defaultVal;
    }

}
