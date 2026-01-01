package com.xxl.conf.admin.openapi.confdata.thread;

import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.conf.core.util.ConfDataUtil;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * confdata cache helper
 *
 * 功能：
 * 1、配置数据信息本地（伪分布式）缓存能力：全量缓存 + 增量更新(实时/秒级广播）；
 * 2、缓存数据变更、主动推动客户端能力：“全量 + 增量/实时” 检测不一致时，各节点匹配监听的client，并主动推送；（触发 DeferredResultHelpler 周知客户端）
 *
 * 面向：
 * 1、服务 client：提供配置查询、配置变更推送能力
 *
 * @author xuxueli
 */
public class ConfDataCacheHelpler {
    private static final Logger logger = LoggerFactory.getLogger(ConfDataCacheHelpler.class);

    /**
     * 配置数据 - 本地缓存：
     *
     *  1、数据格式：
     *  <pre>
     *       {
     *           "test##app01":{                 // Map: "{Env}##{Appname}" -> Map<{Key}, ConfDataCacheDTO>
     *               "key01": {                  // Map: {Key} -> ConfDataCacheDTO
     *                   "key": "key01",             // ConfDataCacheDTO: key
     *                   "value":"value01",          // ConfDataCacheDTO: value
     *                   "valueMd5":"md5"            // ConfDataCacheDTO: valueMd5
     *               },
     *               "key02": {...}
     *           },
     *           "test##app02":{...}
     *       }
     *  </pre>
     */
    private volatile ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> confDataStore = new ConcurrentHashMap<>();

    /**
     * Refresh Data Interval, by second
     */
    public static final int REFRESH_INTERVAL = 30;

    /**
     * first full-sync status, true if sync success
     */
    private volatile boolean warmUp = false;

    /**
     * 全量同步：
     *      - 全量：
     *          - 启动预热：启动时全量查询DB，预热本地缓存；
     *          - 全量刷新：定期全量检测配置md5摘要，对比DB数据，不一致主动刷新；
     *      - 说明：
     *          - 1、范围：DB中全量配置数据，同步至 confDataCacheStore；整个Map覆盖更新；
     *          - 2、间隔：30s；
     *
     *
     *  增量更新：监听广播消息，增量更新；
     *      - MessageHelpler
     *      - 说明：
     *          - 1、说明：实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 confDataCacheThread；单条数据维度覆盖更新；
     *          - 2、间隔：1s/次，实时检测广播消息；无消息则跳过；
     */
    private CyclicThread confDataCacheThread;

    /**
     * start
     */
    public void start(){

        confDataCacheThread = new CyclicThread("ConfDataCacheHelpler-confDataCacheThread", true, new Runnable() {
            @Override
            public void run() {

                // init new map
                ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> confDataStoreNew = new ConcurrentHashMap<>();

                // 1、load all env-appname
                List<ConfData> envAndAppNameList = ConfDataBootstrap.getInstance().getConfDataMapper().queryEnvAndAppName();

                // 2、process each env-appname
                if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                    for (ConfData envAndAppNameData : envAndAppNameList) {

                        // 2.1、query conf-data by env-appname
                        List<ConfData> confDataList = ConfDataBootstrap.getInstance().getConfDataMapper().queryByEnvAndAppName(
                                envAndAppNameData.getEnv(),
                                envAndAppNameData.getAppname());
                        if (CollectionTool.isEmpty(confDataList)) {
                            continue;
                        }

                        // 2.2、build keyMap of appname
                        String envAppname_Key = ConfDataUtil.buildEnvAppname(envAndAppNameData.getEnv(), envAndAppNameData.getAppname());
                        ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_new = confDataStoreNew.computeIfAbsent(envAppname_Key, k -> new ConcurrentHashMap<>());

                        // 2.3、write value for keyMap
                        for (ConfData confData : confDataList) {
                            // convert cacheDTO
                            ConfDataCacheDTO cacheDTO = new ConfDataCacheDTO(confData.getEnv(), confData.getAppname(), confData.getKey(), confData.getValue());
                            // set data
                            keyMap_of_appname_new.put(confData.getKey(), cacheDTO);
                        }
                    }
                }

                // 3、Diff识别不一致数据>客户端推送：检测旧配置（旧配置才被使用，才需要监听），新配置与之不一致则推送；
                List<ConfDataCacheDTO> diffConfList = new ArrayList<>();
                for (String envAppNameKey : confDataStore.keySet()) {
                    ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.get(envAppNameKey);
                    ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_new = confDataStoreNew.get(envAppNameKey);

                    // valid (新服务/无配置，无监听者、忽略)
                    if (MapTool.isEmpty(keyMap_of_appname_local)) {
                        continue;
                    }

                    // process each confData
                    for (String key : keyMap_of_appname_local.keySet()) {
                        ConfDataCacheDTO confDataOld = keyMap_of_appname_local.get(key);
                        ConfDataCacheDTO confDataNew = keyMap_of_appname_new!=null ? keyMap_of_appname_new.get(key): null;
                        // filter diff
                        if (!(confDataNew!=null && confDataNew.getValueMd5().equals(confDataOld.getValueMd5()))){
                            diffConfList.add(confDataOld);
                        }
                    }
                }
                // find diff and push
                if (CollectionTool.isNotEmpty(diffConfList)) {
                    pushClient(diffConfList);
                    logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-confDataCacheThread find diffData and pushClient, diffConfList:{}", diffConfList);
                }

                // 4、replace with new data
                confDataStore = confDataStoreNew;
                logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-confDataCacheThread success, confDataStore:{}", confDataStore);

                // first sycs success, log warmUp
                if (!warmUp) {
                    warmUp = true;
                    logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-confDataCacheThread warmUp finish");
                }

            }
        }, REFRESH_INTERVAL * 1000, true);
        confDataCacheThread.start();

    }

    /**
     * stop
     */
    public void stop(){
        // confDataCacheThread
        if (confDataCacheThread != null) {
            confDataCacheThread.stop();
        }
    }

    /**
     * find changed-data, push client
     */
    private void pushClient(List<ConfDataCacheDTO> diffConfList){
        // valid
        if (CollectionTool.isEmpty(diffConfList)) {
            return;
        }
        // do push
        ConfDataBootstrap.getInstance().getConfDataDeferredResultHelpler().pushClient(diffConfList);
    }

    // ---------------------- tool ----------------------

    /**
     * check update and push
     *
     * @param messageForConfDataDTOList messageForConfDataDTOList
     */
    public void checkUpdateAndPush(List<MessageForConfDataDTO> messageForConfDataDTOList){
        // valid
        if (CollectionTool.isEmpty(messageForConfDataDTOList)) {
            return;
        }

        // check and push
        for (MessageForConfDataDTO messageForConfDataDTO: messageForConfDataDTOList) {

            // valid
            if (messageForConfDataDTO.getEnv()==null || messageForConfDataDTO.getAppname()==null || messageForConfDataDTO.getKey()==null) {
                continue;
            }

            // newData
            ConfData confData = ConfDataBootstrap.getInstance().getConfDataMapper()
                    .queryByEnvAndAppNameAndKey(messageForConfDataDTO.getEnv(), messageForConfDataDTO.getAppname(), messageForConfDataDTO.getKey());
            ConfDataCacheDTO  confDataNew = confData!=null?
                    new ConfDataCacheDTO(confData.getEnv(), confData.getAppname(), confData.getKey(), confData.getValue()):
                    new ConfDataCacheDTO(messageForConfDataDTO.getEnv(), messageForConfDataDTO.getAppname(), messageForConfDataDTO.getKey(), "");

            // cacheData
            String envAppname_Key = ConfDataUtil.buildEnvAppname(confDataNew.getEnv(), confDataNew.getAppname());
            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.get(envAppname_Key);
            ConfDataCacheDTO confDataOld = keyMap_of_appname_local!=null?keyMap_of_appname_local.get(confDataNew.getKey()):null;

            if (!(confDataOld!=null && confDataNew.getValueMd5().equals(confDataOld.getValueMd5()))) {

                // update cache
                keyMap_of_appname_local.put(confDataNew.getKey(), confDataNew);

                // push client
                pushClient(List.of(confDataNew));
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-checkUpdateAndPush find diffData and pushClient, diffConfList:{}", confDataNew);
            }
        }
    }

    // ---------------------- helper ----------------------

    /**
     * query key
     *
     * @param request request
     * @return response
     */
    public Response<QueryKeyResponse> queryKey(QueryKeyRequest request) {
        // valid
        if (!warmUp) {
            return Response.ofFail("query fail, openapi not warmUp");
        }
        if (request==null || CollectionTool.isEmpty(request.getAppnameList())) {
            return Response.ofFail("query fail, invalid request");
        }

        // fill data
        Map<String, List<String>> appnameKeyData = new HashMap<>();
        for (String appname : request.getAppnameList()) {
            String envAppname_Key = ConfDataUtil.buildEnvAppname(request.getEnv(), appname);
            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.get(envAppname_Key);
            if (MapTool.isNotEmpty(keyMap_of_appname_local)) {
                appnameKeyData.put(appname, new ArrayList<>(keyMap_of_appname_local.keySet()));
            }
        }

        // build response
        QueryKeyResponse queryKeyResponse = new QueryKeyResponse();
        queryKeyResponse.setAppnameKeyData(appnameKeyData);
        return Response.ofSuccess(queryKeyResponse);
    }


    /**
     * query data
     *
     * @param request request
     * @return response
     */
    public Response<QueryDataResponse> queryData(QueryDataRequest request) {
        // valid
        if (!warmUp) {
            return Response.ofFail("query fail, openapi not warmUp");
        }
        if (request==null || MapTool.isEmpty(request.getAppnameKeyData())) {
            return Response.ofFail("query fail, invalid request");
        }

        // fill data
        Map<String, Map<String, ConfDataCacheDTO>> confData = new HashMap<>();
        for (String appname : request.getAppnameKeyData().keySet()) {
            // valid keys
            List<String> keys =request.getAppnameKeyData().get(appname);
            if (CollectionTool.isEmpty(keys)) {
                continue;
            }

            // load and valid confData-cache
            String envAppname_Key = ConfDataUtil.buildEnvAppname(request.getEnv(), appname);
            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap_of_appname_local = confDataStore.get(envAppname_Key);
            if (MapTool.isEmpty(keyMap_of_appname_local)) {
                continue;
            }

            // process each key
            Map<String, ConfDataCacheDTO> keyMap_of_appname_return = confData.computeIfAbsent(appname, k -> new HashMap<>());
            for (String key : keys) {
                // valid key item
                if (!keyMap_of_appname_local.containsKey(key)) {
                    continue;
                }
                // load key data
                ConfDataCacheDTO confDataCacheDTO_cache = keyMap_of_appname_local.get(key);

                // build confDataCacheDTO
                ConfDataCacheDTO confDataCacheDTO = new ConfDataCacheDTO();
                confDataCacheDTO.setEnv(confDataCacheDTO_cache.getEnv());
                confDataCacheDTO.setAppname(confDataCacheDTO_cache.getAppname());
                confDataCacheDTO.setKey(confDataCacheDTO_cache.getKey());
                if (!request.isSimpleQuery()) {
                    confDataCacheDTO.setValue(confDataCacheDTO_cache.getValue());
                }
                confDataCacheDTO.setValueMd5(confDataCacheDTO_cache.getValueMd5());

                // write
                keyMap_of_appname_return.put(key, confDataCacheDTO);
            }
        }

        // build response
        QueryDataResponse queryDataResponse = new QueryDataResponse();
        queryDataResponse.setConfData(confData);
        return Response.ofSuccess(queryDataResponse);
    }

}