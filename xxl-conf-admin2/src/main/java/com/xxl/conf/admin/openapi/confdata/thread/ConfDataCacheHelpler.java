package com.xxl.conf.admin.openapi.confdata.thread;

import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataFactory;
import com.xxl.conf.admin.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.conf.admin.openapi.common.model.OpenApiResponse;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataRequest;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataResponse;
import com.xxl.conf.admin.openapi.registry.thread.RegistryCacheHelpler;
import com.xxl.tool.core.CollectionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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
    private static Logger logger = LoggerFactory.getLogger(ConfDataCacheHelpler.class);

    /**
     * 本地缓存：完整数据
     *
     * <pre>
     *      Cache-Data：
     *          Key：String
     *              格式：env##appname##key
     *              示例："test##app02##k1"
     *          Value：ConfDataCacheDTO
     *              value
     *              md5
     *      // 示例
     *      {
     *          "test##app02##k1": {
     *              "value": "v1",
     *              "md5": "md5"
     *          },
     *          "test##app02##k1": {
     *              "value": "v1",
     *                "md5": "md5"
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
     * thread stop variable
     */
    private volatile boolean toStop = false;

    /**
     * first full-sync status, true if sync success
     */
    private volatile boolean warmUp = false;

    /**
     * 全量同步
     * 1、范围：DB中全量配置数据，同步至 confDataCacheStore；整个Map覆盖更新；
     * 2、间隔：REFRESH_INTERVAL
     */
    private Thread fullSyncThread;

    /**
     * start
     */
    public void start(){
        // 1、run fullSyncThread
        fullSyncThread = RegistryCacheHelpler.startThread(new Runnable() {
            @Override
            public void run() {
                // DB中全量注册数据，同步至 CacheStore；整个Map覆盖更新；
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread start");
                while (!toStop) {
                    try {
                        // a、init new map
                        ConcurrentMap<String, ConfDataCacheDTO> confDataCacheStoreNew = new ConcurrentHashMap<>();

                        // b、load all env-appname
                        List<ConfData> envAndAppNameList = ConfDataFactory.getInstance().getConfDataMapper().queryEnvAndAppName();

                        // c、process each env-appname
                        if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                            for (ConfData envAndAppNameData : envAndAppNameList) {
                                // query data by env-appname
                                List<ConfData> confDataList = ConfDataFactory.getInstance().getConfDataMapper().
                                        queryByEnvAndAppName(envAndAppNameData.getEnv(), envAndAppNameData.getAppname());

                                // parse data
                                if (CollectionTool.isNotEmpty(confDataList)) {
                                    for (ConfData confData : confDataList) {
                                        // buld cache
                                        String cacheKey = buildCacheKey(confData.getEnv(), confData.getAppname(), confData.getKey());
                                        ConfDataCacheDTO  cacheValue = new ConfDataCacheDTO(confData);

                                        confDataCacheStoreNew.put(cacheKey, cacheValue);
                                    }
                                }
                            }
                        }

                        /**
                         * d、Diff识别不一致数据，客户端推送
                         *
                         * Diff判定逻辑：以旧CacheMap为基础遍历；新Key不存在，不一致；新Key存在但Value不同，不一致；
                         */
                        List<ConfDataCacheDTO> diffConfList = new ArrayList<>();
                        for (String envAppNameKey : confDataCacheStore.keySet()) {
                            ConfDataCacheDTO confDataOld = confDataCacheStore.get(envAppNameKey);
                            ConfDataCacheDTO confDataNew = confDataCacheStoreNew.get(envAppNameKey);

                            if (!(confDataNew!=null && confDataNew.getValueMd5().equals(confDataOld.getValueMd5()))) {
                                diffConfList.add(confDataNew);
                            }
                        }

                        // find diff and push
                        if (CollectionTool.isNotEmpty(diffConfList)) {
                            pushClient(diffConfList);
                        }

                        // e、replace with new data
                        confDataCacheStore = confDataCacheStoreNew;
                        logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread success, confDataCacheStore:{}",
                                confDataCacheStore);

                        // first full-sycs success, warmUp
                        if (!warmUp) {
                            warmUp = true;
                            logger.debug(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread warmUp finish");
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REFRESH_INTERVAL);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread stop");
            }
        }, ">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-fullSyncThread");

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
        ConfDataFactory.getInstance().getConfDataDeferredResultHelpler().pushClient(diffConfList);
        logger.info(">>>>>>>>>>> xxl-conf, ConfDataCacheHelpler-pushClient, process diffConfList:{}", diffConfList);
    }

    /**
     * stop
     */
    public void stop(){
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // stop thread
        RegistryCacheHelpler.stopThread(fullSyncThread);
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

    public void checkUpdateAndPush(List<MessageForConfDataDTO> messageForConfDataDTOList){
        // valid
        if (CollectionTool.isEmpty(messageForConfDataDTOList)) {
            return;
        }

        // check and push
        for (MessageForConfDataDTO messageForConfDataDTO: messageForConfDataDTOList) {

            // newData
            ConfData confData = ConfDataFactory.getInstance().getConfDataMapper()
                    .queryByEnvAndAppNameAndKey(messageForConfDataDTO.getEnv(), messageForConfDataDTO.getAppname(), messageForConfDataDTO.getKey());
            ConfDataCacheDTO  confDataNew = confData!=null?
                    new ConfDataCacheDTO(confData):
                    new ConfDataCacheDTO(messageForConfDataDTO.getEnv(), messageForConfDataDTO.getAppname(), messageForConfDataDTO.getKey(), "");

            // cacheData
            String cacheKey = buildCacheKey(confDataNew.getEnv(), confDataNew.getAppname(), confDataNew.getKey());
            ConfDataCacheDTO confDataOld = confDataCacheStore.get(cacheKey);

            if (!(confDataOld!=null && confDataNew.getValueMd5().equals(confDataOld.getValueMd5()))) {

                // update cache
                confDataCacheStore.put(cacheKey, confDataNew);

                // push client
                pushClient(Arrays.asList(confDataNew));
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
    public QueryConfDataResponse queryConfData(QueryConfDataRequest request){
        // valid
        if (!warmUp) {
            return new QueryConfDataResponse(OpenApiResponse.FAIL_CODE, "query fail, openapi not warmUp");
        }
        if (request==null || request.getConfKey()==null || request.getConfKey().isEmpty()) {
            return new QueryConfDataResponse(OpenApiResponse.FAIL_CODE, "query fail, invalid request");
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
                    // resp level-1: key is appname
                    Map<String, String> confDataMd5Map = confDataResultMd5.computeIfAbsent(appname, k -> new HashMap<>());
                    // resp level-2: key is key
                    confDataMd5Map.put(confDataCacheDTO.getKey(), confDataCacheDTO.getValueMd5());

                    // query data, not simple
                    if (!request.isSimpleQuery()) {
                        Map<String, String> confDataMap = confDataResult.computeIfAbsent(appname, k -> new HashMap<>());
                        confDataMap.put(confDataCacheDTO.getKey(), confDataCacheDTO.getValue());
                    }

                }
            }
        }

        // get Instance
        QueryConfDataResponse response = new QueryConfDataResponse();
        response.setConfData(confDataResult);
        response.setConfDataMd5(confDataResultMd5);
        return response;
    }

}
