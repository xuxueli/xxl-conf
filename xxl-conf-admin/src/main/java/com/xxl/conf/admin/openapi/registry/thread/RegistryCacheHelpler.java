package com.xxl.conf.admin.openapi.registry.thread;

import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.conf.core.openapi.registry.model.DiscoveryData;
import com.xxl.conf.core.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.core.openapi.registry.model.InstanceCacheDTO;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Md5Tool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * registry cache helper
 *
 * 功能：
 * 1、注册信息本地（伪分布式）缓存能力：全量缓存 + 增量更新(实时/秒级广播）；
 * 2、缓存数据变更、主动推动客户端能力：“全量 + 增量/实时” 检测不一致时，各节点匹配监听的client，并主动推送；（触发 RegistryDeferredResultHelpler 周知客户端）
 *
 * 面向：
 * 1、服务consumer：提供注册信息查询能力、注册变更推送能力；
 *
 * @author xuxueli
 */
public class RegistryCacheHelpler {
    private static final Logger logger = LoggerFactory.getLogger(RegistryCacheHelpler.class);

    /**
     * 注册信息 - 本地缓存：
     *
     *  1、数据格式：
     * <pre>
     *      {
     *          "test##app01": [                        // key  ： "{Env}##{Appname}"
     *              {                                   // value： List<Instance>
     *                  "env": "test",                          // Env（环境唯一标识）
     *                  "appname": "app01",                     // AppName（服务唯一标识）
     *                  "ip": "192.168.1.1",                    // 注册节点IP
     *                  "port": 8080,                           // 注册节点端口号
     *                  "extendInfo": "..."                     // 扩展信息
     *              }
     *          ],
     *          "test##app02": []
     *      }
     * </pre>
     *
     * 2、注册信息缓存更新逻辑：
     *      - 数据流向：DB -> Cache
     *      - 频率：90s/次（三倍心跳间隔）；
     *
     * 3、注册数据是否有效，判定逻辑：
     *      - 动态注册：心跳间隔30s，三倍间隔时间（90s）内存在心跳判定有效，否则无效；(registerModel + registerHeartbeat)
     *      - 持久化注册：永久有效；
     *      - 禁用注册：无效
     */
    private volatile ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStore = new ConcurrentHashMap<>();

    /**
     * 注册信息摘要（Md5） - 本地缓存：
     *
     *  1、数据格式：
     * <pre>
     *      {
     *          "test##app01": "xxxxx",         // key:value = "{Env}##{Appname}" : md5( JSON(registryCacheStore#value) )
     *          "test##app02": "xxxxx"
     * </pre>
     *
     * 2、注册信息缓存更新逻辑：
     *      - 数据流向：DB -> Cache -> Md5( json )
     *      - 说明：数据为注册信息JSON序列化后再Md5的信息，用户快速比对缓存与客户端信息是否一致。一致则不进行更新，幂等跳过；否则再详细比对。
     */
    private volatile ConcurrentMap<String, String> registryCacheMd5Store = new ConcurrentHashMap<>();

    /**
     * BeatTime Interval, by second
     */
    public static final int REGISTRY_BEAT_TIME = 30;

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
     *          - 1、范围：DB中全量注册数据，同步至 registryCacheStore；整个Map覆盖更新；
     *          - 2、间隔：30s；
     *          - 3、过滤：过滤掉无效数据；
     *
     *
     *  增量更新：监听广播消息，增量更新；
     *      - MessageHelpler
     *      - 说明：
     *          - 1、说明：实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore；单条数据维度覆盖更新；
     *          - 2、间隔：1s/次，实时检测广播消息；无消息则跳过；
     *          - 3、过滤：过滤掉无效数据；
     */
    private CyclicThread registryCacheThread;

    /**
     * start
     */
    public void start(){

        registryCacheThread = new CyclicThread("RegistryCacheHelpler-registryCacheThread", true, new Runnable() {
            @Override
            public void run() {

                // 1、init new map
                ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStoreNew = new ConcurrentHashMap<>();
                ConcurrentMap<String, String> registryCacheMd5StoreNew = new ConcurrentHashMap<>();

                // 2、load all env-appname
                List<Instance> envAndAppNameList = RegistryBootstrap.getInstance().getInstanceMapper().queryEnvAndAppName();

                // 3、process each env-appname
                if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                    for (Instance instance : envAndAppNameList) {
                        // build key
                        String envAppNameKey = buildCacheKey(instance.getEnv(), instance.getAppname());

                        // build validValud
                        List<InstanceCacheDTO> cacheValue = buildValidCache(instance.getEnv(), instance.getAppname());

                        // build validValud-md5
                        String cacheValueMd5 = Md5Tool.md5(GsonTool.toJson(cacheValue));

                        // set data
                        registryCacheStoreNew.put(envAppNameKey, cacheValue);
                        registryCacheMd5StoreNew.put(envAppNameKey, cacheValueMd5);      // only match md5, speed up match process
                    }
                }

                /**
                 * 4、Diff识别不一致数据，客户端推送：
                 *      - 老服务-节点下线：旧CacheMap存在，新CacheMap不存在；
                 *      - 老服务-节点变化：旧CacheMap 与 新CacheMap对比，不一致；
                 *      - 新服务（新上线）：旧CacheMap不存在，忽略；
                 */
                List<String> envAppnameDiffList = registryCacheMd5Store
                        .keySet()
                        .stream()
                        .filter(item -> !registryCacheMd5StoreNew.containsKey(item)
                                        || !registryCacheMd5StoreNew.get(item).equals(registryCacheMd5Store.get(item))
                        )
                        .collect(Collectors.toList());
                if (CollectionTool.isNotEmpty(envAppnameDiffList)) {
                    logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-registryCacheThread find diffData and pushClient, envAppnameDiffList:{}", envAppnameDiffList);
                    pushClient(envAppnameDiffList);
                }

                // 5、replace with new data
                registryCacheStore = registryCacheStoreNew;
                registryCacheMd5Store = registryCacheMd5StoreNew;
                logger.debug(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-registryCacheThread success, registryCacheStore:{}, registryCacheMd5Store:{}",
                        registryCacheStore, registryCacheMd5Store);

                // first sycs success, log warmUp
                if (!warmUp) {
                    warmUp = true;
                    logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-registryCacheThread warmUp finish");
                }

            }
        }, REGISTRY_BEAT_TIME * 1000, true);
        registryCacheThread.start();

    }

    /**
     * stop
     */
    public void stop(){
        if (registryCacheThread != null) {
            registryCacheThread.stop();
        }
    }

    /**
     * checkUpdateAndPush
     */
    public void checkUpdateAndPush(List<MessageForRegistryDTO> messageForRegistryDTOList){
        // valid
        if (CollectionTool.isEmpty(messageForRegistryDTOList)) {
            return;
        }

        // find diff
        List<String> envAppnameDiffList = new ArrayList<>();
        for (MessageForRegistryDTO messageForRegistryDTO: messageForRegistryDTOList) {

            // valid
            if (messageForRegistryDTO.getEnv()==null || messageForRegistryDTO.getAppname()==null ) {
                continue;
            }

            // build new data
            List<InstanceCacheDTO> newCacheDTO = buildValidCache(messageForRegistryDTO.getEnv(), messageForRegistryDTO.getAppname());
            String newCacheDTOMD5 = Md5Tool.md5(GsonTool.toJson(newCacheDTO));

            // load cache
            String envAppNameKey = buildCacheKey(messageForRegistryDTO.getEnv(), messageForRegistryDTO.getAppname());
            String oldCacheDTOMD5 = registryCacheMd5Store.get(envAppNameKey);

            // set data (key exists and not match, need broadcast )
            if (!(oldCacheDTOMD5!=null && oldCacheDTOMD5.equals(newCacheDTOMD5))) {
                registryCacheStore.put(envAppNameKey, newCacheDTO);
                registryCacheMd5Store.put(envAppNameKey, newCacheDTOMD5);      // only match md5, speed up match process

                // discovery diff
                envAppnameDiffList.add(envAppNameKey);
            }
        }

        // push client
        if (CollectionTool.isNotEmpty(envAppnameDiffList)) {
            logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler - checkUpdateAndPush find diffData and pushClient, envAppnameDiffList:{}", envAppnameDiffList);
            pushClient(envAppnameDiffList);
        }
    }

    /**
     * find changed-data, push client
     */
    private void pushClient(List<String> envAppnameDiffList){
        // valid
        if (CollectionTool.isEmpty(envAppnameDiffList)) {
            return;
        }
        // do push
        RegistryBootstrap.getInstance().getRegistryDeferredResultHelpler().pushClient(envAppnameDiffList);
    }

    // ---------------------- tool ----------------------

    /**
     * make cache key
     *
     * @param env env
     * @param appname appname
     * @return cache key
     */
    public static String buildCacheKey(String env, String appname){
        return String.format("%s##%s", env, appname);
    }

    /**
     * build valid cache
     *
     * @param env env
     * @param appname appname
     * @return  cache
     */
    private List<InstanceCacheDTO> buildValidCache(String env, String appname){
        // result
        List<InstanceCacheDTO> cacheValue = new ArrayList<>();

        // query valid instance
        Date registerHeartbeatValid = DateTool.addSeconds(new Date(), -1 * REGISTRY_BEAT_TIME * 3);
        List<Instance> instanceCacheDTOList = RegistryBootstrap.getInstance().getInstanceMapper().queryByEnvAndAppNameValid(
                env,
                appname,
                InstanceRegisterModelEnum.AUTO.getValue(),
                InstanceRegisterModelEnum.PERSISTENT.getValue(),
                registerHeartbeatValid);

        // parse cache
        if (CollectionTool.isNotEmpty(instanceCacheDTOList)){
            // convert to cache-dto, and sort by "ip:port"
            cacheValue = instanceCacheDTOList
                    .stream()
                    .map(instance ->
                            new InstanceCacheDTO(
                                    instance.getEnv(),
                                    instance.getAppname(),
                                    instance.getIp(),
                                    instance.getPort(),
                                    instance.getExtendInfo())
                    )
                    .sorted(Comparator.comparing(InstanceCacheDTO::getSortKey))     // sort， for md5 match
                    .collect(Collectors.toList());
        }

        return cacheValue;
    }


    // ---------------------- helper ----------------------

    /**
     * find OnLine Instance
     *
     * @param env env
     * @param appname appname
     * @return Instance dto list
     */
    private List<InstanceCacheDTO> findOnLineInstance(String env, String appname){
        // valid
        if (!warmUp) {
            return null;
        }
        if (StringTool.isBlank(env) || StringTool.isBlank(appname)) {
            return null;
        }

        // build key
        String envAppNameKey = buildCacheKey(env, appname);

        // get Instance
        return registryCacheStore.get(envAppNameKey);
    }

    /**
     * find OnLine Instance-MD5
     *
     * @param env env
     * @param appname appname
     * @return Instance-MD5
     */
    private String findOnLineInstanceMd5(String env, String appname){
        // valid
        if (!warmUp) {
            return null;
        }
        if (StringTool.isBlank(env) || StringTool.isBlank(appname)) {
            return null;
        }

        // build key
        String envAppNameKey = buildCacheKey(env, appname);

        // get Instance-MD5
        return registryCacheMd5Store.get(envAppNameKey);
    }

    /**
     * discovery OnLine Instance
     *
     * @param request request
     * @return  response
     */
    public Response<DiscoveryData> discoveryOnLineInstance(DiscoveryRequest request) {
        // valid
        if (request == null || CollectionTool.isEmpty(request.getAppnameList())) {
            return Response.ofFail("param Invalid.");
        }

        // query data
        Map<String, List<InstanceCacheDTO>> discoveryDataMap = new HashMap<>();
        Map<String, String> discoveryDataMd5 = new HashMap<>();
        for (String appname : request.getAppnameList()) {
            String onLineInstanceMd5 = findOnLineInstanceMd5(request.getEnv(), appname);
            if (StringTool.isNotBlank(onLineInstanceMd5)) {
                // set md5
                discoveryDataMd5.put(appname, onLineInstanceMd5);
                // set detail
                if (!request.isSimpleQuery()) {
                    discoveryDataMap.put(appname, findOnLineInstance(request.getEnv(), appname));
                }
            }
        }

        // response
        DiscoveryData discoveryData = new DiscoveryData();
        discoveryData.setDiscoveryData(discoveryDataMap);
        discoveryData.setDiscoveryDataMd5(discoveryDataMd5);
        return Response.ofSuccess(discoveryData);
    }

}
