package com.xxl.conf.core.confdata;

import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * local cache conf
 *
 *  客户端：
 *      - 启动预热：启动时查询全量key，分批查询全量配置数据预热，默认重试3次；             （warmUp）
 *      - 全量刷新：定期全量检测配置md5摘要，对比本地与远程数据，不一致主动刷新；60s/次；   （monitor/60s + refresh/all）
 *      - 增量更新：监听appkey，监听配置变更，触发全量比对刷新；                        （monitor/60s + refresh/all）
 *      - 降级：全量比度内存数据与本地file（“/xxl-conf/env/clustor/confdata/appkey.prop”），不一致更新本地数据；5min/次； （warmUp）
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfLocalCacheHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlConfLocalCacheHelper.class);

    // ---------------------- init ----------------------

    private volatile XxlConfBootstrap xxlConfBootstrap;
    public XxlConfLocalCacheHelper(XxlConfBootstrap xxlConfBootstrap) {
        this.xxlConfBootstrap = xxlConfBootstrap;
    }


    // ---------------------- cache data ----------------------

    /**
     * <pre>
     *     {
     *         "app01":{                        // Map: "{Appname}" -> Map<{Key}, ConfDataCacheDTO>
     *             "key01": {                   // Map: {Key} -> ConfDataCacheDTO
     *                 "key": "key01",              // ConfDataCacheDTO: key
     *                 "value":"value01",           // ConfDataCacheDTO: value
     *                 "valueMd5":"md5"             // ConfDataCacheDTO: valueMd5
     *             },
     *             "key02": {...}
     *         },
     *         "app02":{...}
     *     }
     * </pre>
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> confDataStore = new ConcurrentHashMap<>();

    // ---------------------- start / stop ----------------------

    private CyclicThread refreshThread;

    /**
     * start
     */
    public void start(){

        /**
         * 1、warmUp
         *
         *  说明：
         *      - 启动预热：启动时查询全量key，分批查询全量配置数据预热，默认重试3次；                        （warmUp）
         *      - 降级：全量比度内存数据与本地file（“/xxl-conf/env/clustor/confdata/appkey.prop”），不一致更新本地数据；5min/次； （warmUp）
          */
        boolean warmUp = false;
        for (int i = 0; i < 3; i++) {
            // 1.1、try warmUp 3 times from remote
            try {
                QueryKeyRequest queryKeyRequest = new QueryKeyRequest();
                queryKeyRequest.setEnv(xxlConfBootstrap.getEnv());
                queryKeyRequest.setAppnameList(List.of(xxlConfBootstrap.getAppname()));

                // 1.1、queryKey
                Response<QueryKeyResponse> queryKeyResponse = xxlConfBootstrap.loadClient().queryKey(queryKeyRequest);

                // parse response
                if (Response.isSuccess(queryKeyResponse) && queryKeyResponse.getData()!=null) {
                    // parse queryKey response
                    Map<String, List<String>> appnameKeyData = queryKeyResponse.getData().getAppnameKeyData();

                    // 1.2、refresh + notify
                    remoteQueryWithRefreshAndNotify(appnameKeyData);
                }
                logger.info(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp success from remote");
                warmUp = true;
                break;
            } catch (Throwable e) {
                logger.error(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp error, try {} times: {}", i, e.getMessage(), e);
            }
        }
        if (!warmUp) {
            // 1.2、load from file
            try {
                HashMap<String, ConfDataCacheDTO> keyMap_of_appname_file = xxlConfBootstrap.getFileHelper().queryData(xxlConfBootstrap.getEnv(), xxlConfBootstrap.getAppname());
                if (MapTool.isNotEmpty(keyMap_of_appname_file)) {
                    for (String key : keyMap_of_appname_file.keySet()) {
                        ConfDataCacheDTO confData = keyMap_of_appname_file.get(key);
                        // write conf-data
                        confDataStore.computeIfAbsent(xxlConfBootstrap.getAppname(), k -> new ConcurrentHashMap<>()).put(key, confData);
                    }
                    logger.info(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp success from file");
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * 2、refreshThread
         *
         *  说明：
         *      - 全量刷新：定期全量检测配置md5摘要，对比本地与远程数据，不一致主动刷新；60s/次；   （monitor/60s + refresh/all）
         *      - 增量更新：监听appkey，监听配置变更，触发全量比对刷新；                        （monitor/60s + refresh/all）
         */
        refreshThread = new CyclicThread("XxlConfLocalCacheHelper-refreshThread", true, new Runnable() {
            @Override
            public void run() {

                // pass if no-data
                if (confDataStore.isEmpty()) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

                // 2、monitor
                MonitorRequest request = new MonitorRequest();
                request.setEnv(xxlConfBootstrap.getEnv());
                request.setAppnameList(new ArrayList<>(confDataStore.keySet()));
                // 2.1、monitor
                xxlConfBootstrap.loadMonitorClient().monitor(request);
                // sleep：a、avoid fail-retry too quick；b、make sure server broadcast complete
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 3、remote query and refresh + notify
                Map<String, List<String>> appnameKeyData = new HashMap<>();
                for (String appname : confDataStore.keySet()) {
                    if (confDataStore.get(appname)!=null && !confDataStore.get(appname).isEmpty()) {
                        appnameKeyData.put(appname, confDataStore.get(appname).keySet().stream().toList());
                    }
                }
                // 3.1、refresh + notify
                remoteQueryWithRefreshAndNotify(appnameKeyData);
                logger.debug(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread remoteQueryWithRefreshAndNotify[1] finish");
            }
        }, 1000, true);
        refreshThread.start();
    }

    /**
     * stop
     */
    public void stop(){
        if (refreshThread != null) {
            refreshThread.stop();
        }
    }

    // ---------------------- util ----------------------

    /**
     * remote query, with refresh and notify
     *
     * @param appnameKeyData appnameKeyData
     */
    private void remoteQueryWithRefreshAndNotify(Map<String, List<String>> appnameKeyData){

        // request
        QueryDataRequest request = new QueryDataRequest();
        request.setEnv(xxlConfBootstrap.getEnv());
        request.setAppnameKeyData(appnameKeyData);
        request.setSimpleQuery(false);
        // 1、queryData
        Response<QueryDataResponse> queryConfDataResponse = xxlConfBootstrap.loadClient().queryData(request);

        // response
        if (Response.isSuccess(queryConfDataResponse)
                && queryConfDataResponse.getData()!=null
                && MapTool.isNotEmpty(queryConfDataResponse.getData().getConfData()))  {

            // 2、process exist confData, refresh + notify
            Map<String, Map<String, ConfDataCacheDTO>> confData = queryConfDataResponse.getData().getConfData();
            for (String appname: confData.keySet()) {

                // load keyMap
                Map<String, ConfDataCacheDTO> keyMap_of_appname_remote = confData.get(appname);
                ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.computeIfAbsent(appname, k->new ConcurrentHashMap<>());

                // each key
                for (String key: keyMap_of_appname_remote.keySet()) {
                    ConfDataCacheDTO new_value = keyMap_of_appname_remote.get(key);
                    ConfDataCacheDTO local_value = keyMap_of_appname_local.get(key);

                    // diff check, pass repeat-data
                    if (local_value!=null && local_value.getValueMd5().equals(new_value.getValueMd5())) {
                        continue;
                    }

                    // refresh data
                    keyMap_of_appname_local.put(key, new_value);

                    // notify
                    xxlConfBootstrap.getListenerHelper().notifyChange(new ConfDataCacheDTO(xxlConfBootstrap.getEnv(), appname, key, new_value.getValue()));
                }
            }
        }

        // 3、process no-exist confData, write none
        for (String appname: appnameKeyData.keySet()) {
            // load keyMap
            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.computeIfAbsent(appname, k->new ConcurrentHashMap<>());

            // each key
            for (String key: appnameKeyData.get(appname)) {
                if (!keyMap_of_appname_local.containsKey(key)) {
                    keyMap_of_appname_local.put(key, new ConfDataCacheDTO(xxlConfBootstrap.getEnv(), appname, key, ""));
                }
            }
        }

    }

    // ---------------------- tool ----------------------

    /**
     * get conf data
     *
     * @param key conf key
     * @param defaultVal conf default value
     * @return conf value
     */
    public String get(String key, String defaultVal) {
        return get(xxlConfBootstrap.getAppname(), key, defaultVal);
    }

    /**
     * get conf data
     *
     * @param appname conf appname
     * @param key conf key
     * @param defaultVal conf default value
     * @return conf value
     */
    public String get(String appname, String key, String defaultVal) {

        // 1、get by cache
        if (confDataStore.containsKey(appname) && confDataStore.get(appname).containsKey(key)) {
            return confDataStore.get(appname).get(key).getValue();
        }

        // 2、get from remote; get-and-watch
        try {
            Map<String, List<String>> appnameKeyData = new HashMap<>();
            appnameKeyData.put(appname, List.of(key));
            // 2.1、refresh + notify
            remoteQueryWithRefreshAndNotify(appnameKeyData);
        } catch (Exception e) {
            logger.error(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper - get error, appname:{}, key:{}", appname, key, e);
        }

        // 2.1、new data from remote
        if (confDataStore.containsKey(appname) && confDataStore.get(appname).containsKey(key)) {
            return confDataStore.get(appname).get(key).getValue();
        }

        // 3、default
        return defaultVal;
    }

    /**
     * get all conf data
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> getAllConfData() {
        return confDataStore;
    }

}
