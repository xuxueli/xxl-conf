package com.xxl.conf.admin.openapi.confdata.thread;

import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.core.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.conf.core.openapi.confdata.model.ConfDataInfo;
import com.xxl.conf.core.openapi.confdata.model.ConfDataRequest;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
     * <pre>
     *      {
     *          "test##app02##key01": {                     // key  ： "{Env}##{Appname}##{Key}"
     *              "env": "test",                          // Env（环境唯一标识）
     *              "appname": "app01",                     // AppName（服务唯一标识）
     *              "key": "key01",                         // Key
     *              "value": "value01",                     // 配置数据
     *              "valueMd5": "md5"                       // 配置数据MD5
     *          },
     *          "test##app02##key02": {
     *              ...
     *          }
     *      }
     * </pre>
     */
    private volatile ConcurrentMap<String, ConfDataCacheDTO> confDataCacheStore = new ConcurrentHashMap<>();

    /**
     * Refresh Data Interval, by second
     */
    public static final int REFRESH_INTERVAL = 30;

    /**
     * first full-sync status, true if sync success
     */
    private volatile boolean warmUp = false;

    /**
     * 全量同步
     * 1、范围：DB中全量配置数据，同步至 confDataCacheStore；整个Map覆盖更新；
     * 2、间隔：REFRESH_INTERVAL
     */
    private CyclicThread confDataCacheThread;

    /**
     * start
     */
    public void start(){

        confDataCacheThread = new CyclicThread("RegistryCacheHelpler-registryCacheThread", true, new Runnable() {
            @Override
            public void run() {

                // 1、init new map
                ConcurrentMap<String, ConfDataCacheDTO> confDataCacheStoreNew = new ConcurrentHashMap<>();

                // 2、load all env-appname
                List<ConfData> envAndAppNameList = ConfDataBootstrap.getInstance().getConfDataMapper().queryEnvAndAppName();

                // 3、process each env-appname
                if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                    for (ConfData envAndAppNameData : envAndAppNameList) {
                        // query data by env-appname
                        List<ConfData> confDataList = ConfDataBootstrap.getInstance().getConfDataMapper().queryByEnvAndAppName(envAndAppNameData.getEnv(), envAndAppNameData.getAppname());

                        // process each key of "env-appname
                        if (CollectionTool.isNotEmpty(confDataList)) {
                            for (ConfData confData : confDataList) {
                                // fill cache of each confData
                                String cacheKey = buildCacheKey(confData.getEnv(), confData.getAppname(), confData.getKey());
                                ConfDataCacheDTO  cacheValue = new ConfDataCacheDTO(confData.getEnv(), confData.getAppname(), confData.getKey(), confData.getValue());

                                // set data
                                confDataCacheStoreNew.put(cacheKey, cacheValue);
                            }
                        }
                    }
                }

                /**
                 * 4、Diff识别不一致数据，客户端推送：
                 *      - 老服务-节点下线：旧CacheMap存在，新CacheMap不存在；
                 *      - 老服务-节点变化：旧CacheMap 与 新CacheMap对比，不一致；
                 *      - 新服务（新上线）：旧CacheMap不存在，忽略；
                 */
                List<ConfDataCacheDTO> diffConfList = new ArrayList<>();
                for (String envAppNameKey : confDataCacheStore.keySet()) {
                    ConfDataCacheDTO confDataOld = confDataCacheStore.get(envAppNameKey);
                    ConfDataCacheDTO confDataNew = confDataCacheStoreNew.get(envAppNameKey);

                    if (confDataNew==null || !confDataNew.getValueMd5().equals(confDataOld.getValueMd5())) {
                        diffConfList.add(confDataOld);
                    }
                }
                // find diff and push
                if (CollectionTool.isNotEmpty(diffConfList)) {
                    pushClient(diffConfList);
                    logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread find diffData and pushClient, diffConfList:{}", diffConfList);
                }

                // 5、replace with new data
                confDataCacheStore = confDataCacheStoreNew;
                logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread success, confDataCacheStore:{}", confDataCacheStore);

                // first sycs success, log warmUp
                if (!warmUp) {
                    warmUp = true;
                    logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread warmUp finish");
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
     * make cache key
     *
     * @param env
     * @param appname
     * @return
     */
    public static String buildCacheKey(String env, String appname, String key){
        return String.format("%s##%s##%s", env, appname, key);
    }

    /**
     * check update and push
     *
     * @param messageForConfDataDTOList
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
            String cacheKey = buildCacheKey(confDataNew.getEnv(), confDataNew.getAppname(), confDataNew.getKey());
            ConfDataCacheDTO confDataOld = confDataCacheStore.get(cacheKey);

            if (!(confDataOld!=null && confDataNew.getValueMd5().equals(confDataOld.getValueMd5()))) {

                // update cache
                confDataCacheStore.put(cacheKey, confDataNew);

                // push client
                pushClient(Arrays.asList(confDataNew));
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-checkUpdateAndPush find diffData and pushClient, diffConfList:{}", confDataNew);
            }
        }
    }

    // ---------------------- helper ----------------------

    /**
     * query ConfData
     *
     * @param request
     * @return
     */
    public Response<ConfDataInfo> queryConfData(ConfDataRequest request){
        // valid
        if (!warmUp) {
            return Response.ofFail("query fail, openapi not warmUp");
        }
        if (request==null || request.getConfKey()==null || request.getConfKey().isEmpty()) {
            return Response.ofFail("query fail, invalid request");
        }

        // result data
        Map<String, Map<String, String>> confDataResult = new HashMap<>();
        Map<String, Map<String, String>> confDataResultMd5 = new HashMap<>();

        // process request
        for (Map.Entry<String, List<String>> entity : request.getConfKey().entrySet()) {
            // req level-1: key is appname
            String appname = entity.getKey();
            for (String keyItem : entity.getValue()) {
                // req levle-2: key is key

                // load cache-data
                String confKey = buildCacheKey(request.getEnv(), appname, keyItem);
                ConfDataCacheDTO confDataCacheDTO = confDataCacheStore.get(confKey);

                // fill resp
                if (confDataCacheDTO!=null) {
                    // resp level-1: appname as key
                    Map<String, String> confDataMd5Map = confDataResultMd5.computeIfAbsent(appname, k -> new HashMap<>());
                    // resp level-2: key as key
                    confDataMd5Map.put(confDataCacheDTO.getKey(), confDataCacheDTO.getValueMd5());

                    // query data, not simple
                    if (!request.isSimpleQuery()) {
                        Map<String, String> confDataMap = confDataResult.computeIfAbsent(appname, k -> new HashMap<>());
                        confDataMap.put(confDataCacheDTO.getKey(), confDataCacheDTO.getValue());
                    }

                }
            }
        }

        // result
        ConfDataInfo confDataInfo = new ConfDataInfo();
        confDataInfo.setConfData(confDataResult);
        confDataInfo.setConfDataMd5(confDataResultMd5);
        return Response.ofSuccess(confDataInfo);
    }

}
