package com.xxl.conf.core.confdata;

import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *      - A、启动预热：启动时查询全量key，分批查询全量配置数据预热，默认重试3次；                  （warmUp）
 *      - B、配置降级：如果启动预热失败，降级查询本地file；定期同步配置到本地file，默认3min/次；    （warmUp）
 *      - C、全量刷新：定期全量检测配置数据md5摘要，对比本地与远程数据，针对不一致主动刷新；60s/次；  （monitor/60s + detect&refresh/all）
 *      - D、增量更新：监听appkey维度变更，识别变更后触发全量检测刷新；
 *
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
     *         "app01":{                        // key   : "{Appname}" ->
     *             "key01": {                   // value : Map<{Key}, ConfDataCacheDTO>
     *                 "key": "key01",                  // ConfDataCacheDTO: key
     *                 "value":"value01",               // ConfDataCacheDTO: value
     *                 "valueMd5":"md5"                 // ConfDataCacheDTO: valueMd5
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
         *      - A、启动预热：启动时查询全量key，分批查询全量配置数据预热，默认重试3次；                  （warmUp）
         *      - B、配置降级：如果启动预热失败，降级查询本地file；定期同步配置到本地file，默认3min/次；    （warmUp）
          */
        boolean warmUp = false;
        int warmUpRetryCount = 3;
        for (int i = 0; i < warmUpRetryCount; i++) {
            // A、启动预热: try 3 times from remote
            try {

                // 1.1、Remote queryKey
                QueryKeyRequest queryKeyRequest = new QueryKeyRequest(xxlConfBootstrap.getEnv(), List.of(xxlConfBootstrap.getAppname()));
                Response<QueryKeyResponse> queryKeyResponse = xxlConfBootstrap.loadClient().queryKey(queryKeyRequest);
                if (!Response.isSuccess(queryKeyResponse)) {
                    throw new XxlConfException("XxlConfLocalCacheHelper warmUp queryKey fail: " + queryKeyResponse.getMsg());
                }

                // parse response
                if (queryKeyResponse.getData()!=null && MapTool.isNotEmpty(queryKeyResponse.getData().getAppnameKeyData())) {
                    // 1.2、启动预热 Remote queryData + RefreshAndNotify
                    remoteQueryDataWithRefreshAndNotify(xxlConfBootstrap.getEnv(), queryKeyResponse.getData().getAppnameKeyData());
                }

                logger.info(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp from remote success.");
                warmUp = true;
                break;
            } catch (Throwable e) {
                logger.error(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp from remote error, try {} times: {}", i+1, e.getMessage(), e);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        }
        if (!warmUp) {
            // B、配置降级：load from file
            try {
                // 1.1、File queryData
                ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_file = xxlConfBootstrap.getFileHelper()
                        .queryData(xxlConfBootstrap.getEnv(), xxlConfBootstrap.getAppname());
                if (MapTool.isNotEmpty(keyMap_of_appname_file)) {

                    // 1.2、配置降级 for eath appname
                    refreshAndNotify(xxlConfBootstrap.getEnv(), xxlConfBootstrap.getAppname(), keyMap_of_appname_file);

                    logger.info(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp from file success.");
                    warmUp = true;
                }
            } catch (Throwable e) {
                logger.error(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper warmUp from file error: {}", e.getMessage(), e);
            }
        }
        if (!warmUp) {
            throw new XxlConfException("xxl-conf, XxlConfLocalCacheHelper warmUp fail, please check xxl-conf address and network conditions.");
        }

        /**
         * 2、refreshThread
         *
         *  说明：
         *      - C、全量刷新：定期全量检测配置数据md5摘要，对比本地与远程数据，针对不一致主动刷新；60s/次；  （monitor/60s + detect&refresh/all）
         *      - D、增量更新：监听appkey维度变更，识别变更后触发全量检测刷新；
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

                // 1、monitor
                MonitorRequest request = new MonitorRequest(xxlConfBootstrap.getEnv(), new ArrayList<>(confDataStore.keySet()));
                xxlConfBootstrap.loadMonitorClient().monitor(request);
                // sleep：a、avoid fail-retry too quick；b、make sure server broadcast complete
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (MapTool.isEmpty(confDataStore)) {
                    return;
                }

                // 2、detect changed data, refresh + notify
                // request
                Map<String, List<String>> appnameKeyData = new HashMap<>();
                for (String appname : confDataStore.keySet()) {
                    if (MapTool.isNotEmpty(confDataStore.get(appname))) {
                        appnameKeyData.put(appname, confDataStore.get(appname).keySet().stream().toList());
                    }
                }

                // 2.1、queryData - detect
                QueryDataRequest queryDataRequest_detect = new QueryDataRequest(xxlConfBootstrap.getEnv(), appnameKeyData, true);
                Response<QueryDataResponse> queryDataResponse_detect = xxlConfBootstrap.loadClient().queryData(queryDataRequest_detect);

                // parse response
                if (!Response.isSuccess(queryDataResponse_detect)) {
                    throw new XxlConfException("XxlConfLocalCacheHelper refreshThread queryKey fail: " + queryDataResponse_detect.getMsg());
                }
                if (queryDataResponse_detect.getData()!=null
                        && MapTool.isNotEmpty(queryDataResponse_detect.getData().getConfData())) {

                    // 2.2、detect changed data
                    Map<String, List<String>> appnameKeyData_changed = new HashMap<>();
                    for (String appname : queryDataResponse_detect.getData().getConfData().keySet()) {

                        // keyMap
                        Map<String, ConfDataCacheDTO> keyMap_detect = queryDataResponse_detect.getData().getConfData().get(appname);
                        ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_local = confDataStore.get(appname);

                        // only process remote conf-data, local data may be none
                        if (MapTool.isEmpty(keyMap_detect)) {
                            continue;
                        }
                        for (String key : keyMap_detect.keySet()) {

                            // key data
                            ConfDataCacheDTO confDataCacheDTO_detect = keyMap_detect.get(key);

                            // filter changed key
                            if (!(keyMap_local!=null
                                    && keyMap_local.containsKey(key)
                                    && keyMap_local.get(key).getValueMd5().equals(confDataCacheDTO_detect.getValueMd5()))) {
                                // collect changed key
                                appnameKeyData_changed
                                        .computeIfAbsent(appname, k -> new ArrayList<>())
                                        .add(key);
                            }
                        }
                    }

                    // 2.3、queryData, refresh + notify
                    if (MapTool.isNotEmpty(appnameKeyData_changed)) {
                        remoteQueryDataWithRefreshAndNotify(xxlConfBootstrap.getEnv(), appnameKeyData_changed);
                        logger.info(">>>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper-refreshThread detect changed confData and refresh finish, confData:{}", appnameKeyData_changed);
                    }
                }

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
     * remote queryData, and refresh + notify
     *
     *  1、直接查询数据，不比对md5；
     *  2、空参数忽略，不处理写 null；
     *
     * @param env env
     * @param appnameKeyData appname -> List<Key:String>
     */
    private void remoteQueryDataWithRefreshAndNotify(String env, Map<String, List<String>> appnameKeyData) {
        if (StringTool.isBlank(env) || MapTool.isEmpty(appnameKeyData)) {
            return;
        }

        // 1、queryData
        QueryDataRequest request = new QueryDataRequest(env, appnameKeyData, false);
        Response<QueryDataResponse> queryConfDataResponse = xxlConfBootstrap.loadClient().queryData(request);

        // 2、valid response
        if (!Response.isSuccess(queryConfDataResponse)) {
            throw new XxlConfException("XxlConfLocalCacheHelper queryData error: " + queryConfDataResponse.getMsg());
        }

        // 3、refresh + notify
        if (queryConfDataResponse.getData()!=null && MapTool.isNotEmpty(queryConfDataResponse.getData().getConfData())) {
            for (String appname: queryConfDataResponse.getData().getConfData().keySet()) {
                Map<String, ConfDataCacheDTO> keyMap = queryConfDataResponse.getData().getConfData().get(appname);

                // 3.1、refresh + notify, for each appname
                refreshAndNotify(env, appname, new ConcurrentHashMap<>(keyMap));
            }
        }
    }

    /**
     * refresh and notify
     *
     * @param env env
     * @param appname appname
     * @param keyMap_of_appname_new keyMap_of_appname_new
     */
    private void refreshAndNotify(String env, String appname, ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_new) {
        if (MapTool.isEmpty(keyMap_of_appname_new)) {
            return;
        }

        // 1、init keyMap-of-appname
        ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.computeIfAbsent(xxlConfBootstrap.getAppname(), k -> new ConcurrentHashMap<>());

        // 2、refresh keyMap-of-appname
        for (String key : keyMap_of_appname_new.keySet()) {
            ConfDataCacheDTO new_value = keyMap_of_appname_new.get(key);

            // 2.1、refresh key
            keyMap_of_appname_local.put(key, new_value);

            // 2.2、notify key
            xxlConfBootstrap.getListenerHelper().notifyChange(new ConfDataCacheDTO(env, appname, key, new_value.getValue()));
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

            // 2.1、懒加载查询：Remote queryData + RefreshAndNotify
            remoteQueryDataWithRefreshAndNotify(xxlConfBootstrap.getEnv(), MapTool.newMap(
                    appname, List.of(key)
            ));

        } catch (Throwable e) {
            logger.error(">>>>>>>>>> xxl-conf, XxlConfLocalCacheHelper - get error, appname:{}, key:{}", appname, key, e);
        }

        // 2.1、new data from remote
        if (confDataStore.containsKey(appname) && confDataStore.get(appname).containsKey(key)) {
            return confDataStore.get(appname).get(key).getValue();
        }

        // 3、default
        // 懒加载查询失败，写None值：write null-conf to cache
        refreshAndNotify(xxlConfBootstrap.getEnv(), appname, new ConcurrentHashMap<>(MapTool.newMap(
                key, new ConfDataCacheDTO(xxlConfBootstrap.getEnv(), appname, key, "")
        )));
        return defaultVal;
    }

    /**
     * get all conf data
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> getAllConfData() {
        return confDataStore;
    }

}
