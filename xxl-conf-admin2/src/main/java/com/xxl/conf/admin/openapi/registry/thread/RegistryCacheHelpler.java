package com.xxl.conf.admin.openapi.registry.thread;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.model.dto.InstanceCacheDTO;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.model.entity.Message;
import com.xxl.conf.admin.openapi.registry.config.RegistryFactory;
import com.xxl.conf.admin.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.admin.openapi.registry.model.DiscoveryResponse;
import com.xxl.conf.admin.openapi.registry.model.OpenApiResponse;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.Md5Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xxl.conf.admin.openapi.registry.model.OpenApiResponse.SUCCESS_CODE;

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
    private static Logger logger = LoggerFactory.getLogger(RegistryCacheHelpler.class);

    /**
     * 注册信息本地缓存：完整数据
     *
     * <pre>
     *      DB-Instance：DB完整注册信息。
     *          env：Env（环境唯一标识）
     *          appname：AppName（应用唯一标识）
     *          ip：注册节点IP
     *          port：注册节点端口号
     *          registerModel：注册模式
     *          registerHeartbeat：节点最后心跳时间，动态注册时判定是否过期
     *          extendInfo：扩展信息
     *      Cache-Data：
     *          Key：String
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：ArrayList<Object>
     *              ip
     *              port
     *              extendInfo
     *      注册数据是否有效，判定逻辑：
     *          动态注册：心跳间隔30s，三倍间隔时间内存在心跳判定有效，否则无效；(registerModel + registerHeartbeat)
     *          持久化注册：永久有效；
     *          禁用注册：无效
     * </pre>
     */
    private volatile ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStore = new ConcurrentHashMap<>();
    /**
     * 注册信息本地缓存：MD5摘要
     *
     * <pre>
     *     说明：数据为注册信息JSON序列化后再Md5的信息，用户快速比对缓存与客户端信息是否一致。一致则不进行更新，幂等跳过；否则再详细比对。
     *     Cache-Data：
     *          Key：String
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：String
     *              格式：md5( registryCacheStore#value )
     * </pre>
     */
    private volatile ConcurrentMap<String, String> registryCacheMd5Store = new ConcurrentHashMap<>();

    /**
     * BeatTime Interval, by second
     */
    public static final int REGISTRY_BEAT_TIME = 30;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;

    /**
     * first full-sync status, true if sync success
     */
    private volatile boolean warmUp = false;

    /**
     * message filtering to avoid duplicate processing
     */
    private volatile List<Long> readedMessageIds = Collections.synchronizedList(new ArrayList());

    /**
     * 全量同步
     * 1、范围：DB中全量注册数据，同步至 registryCacheStore；整个Map覆盖更新；
     * 2、间隔：3倍心跳（REGISTRY_BEAT_TIME * 3）；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread fullSyncThread;

    /**
     * 增量(实时)同步
     * 1、说明：实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore；单条数据维度覆盖更新；
     * 2、间隔：1s/次，实时检测广播消息；无消息则跳过；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread messageListenThread;

    /**
     * start
     */
    public void start(){
        // 1、run fullSyncThread
        fullSyncThread = startThread(new Runnable() {
            @Override
            public void run() {
                // DB中全量注册数据，同步至 registryCacheStore；整个Map覆盖更新；
                logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread start");
                while (!toStop) {
                    try {
                        // a、init new map
                        ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStoreNew = new ConcurrentHashMap<>();
                        ConcurrentMap<String, String> registryCacheMd5StoreNew = new ConcurrentHashMap<>();

                        // b、load all env-appname
                        List<Instance> envAndAppNameList = RegistryFactory.getInstance().getInstanceMapper().queryEnvAndAppName();

                        // c、process each env-appname
                        if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                            for (Instance instance : envAndAppNameList) {
                                // build key
                                String envAppNameKey = buildCacheKey(instance.getEnv(), instance.getAppname());

                                // build validValud
                                List<InstanceCacheDTO> cacheValue = buildValidCache(instance.getEnv(), instance.getAppname());

                                // build validValud-md5
                                String cacheValueMd5 = Md5Tool.md5(JSON.toJSONString(cacheValue));

                                // set data
                                registryCacheStoreNew.put(envAppNameKey, cacheValue);
                                registryCacheMd5StoreNew.put(envAppNameKey, cacheValueMd5);      // only match md5, speed up match process
                            }
                        }

                        /**
                         * d、Diff识别不一致数据，客户端推送
                         *
                         * Diff判定逻辑：以旧CacheMap为基础遍历；新Key不存在，不一致；新Key存在但Value不同，不一致；
                         */
                        List<String> envAppnameDiffList = registryCacheMd5Store.keySet().stream()
                                .filter(item -> !registryCacheMd5StoreNew.containsKey(item) || !registryCacheMd5StoreNew.get(item).equals(registryCacheMd5Store.get(item)))
                                .collect(Collectors.toList());
                        if (CollectionTool.isNotEmpty(envAppnameDiffList)) {
                            logger.warn(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread find envAppnameDiffList:{}", envAppnameDiffList);
                            pushClient(envAppnameDiffList);
                        }

                        // e、replace with new data
                        registryCacheStore = registryCacheStoreNew;
                        registryCacheMd5Store = registryCacheMd5StoreNew;
                        logger.debug(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread success, registryCacheStore:{}, registryCacheMd5Store:{}",
                                registryCacheStore, registryCacheMd5Store);

                        // first full-sycs success, warmUp
                        if (!warmUp) {
                            warmUp = true;
                            logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread warmUp finish");
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME * 3);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread stop");
            }
        }, ">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread");

        // 3、messageListenThread
        messageListenThread = startThread(new Runnable() {
            @Override
            public void run() {
                // 实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore；单条数据维度覆盖更新；
                logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread start");
                while (!toStop) {
                    try {
                        // a、detect real-time messages, Once per second
                        Date msgTimeValidStart = DateTool.addSeconds(new Date(), -1 * 10);
                        Date msgTimeValidEnd = DateTool.addMinutes(msgTimeValidStart, 5);
                        List<Long> excludeMsgIds = readedMessageIds.size()>50?readedMessageIds.subList(0, 50):readedMessageIds;

                        List<Message> messageList = RegistryFactory.getInstance().getMessageMapper().queryValidMessage(msgTimeValidStart, msgTimeValidEnd, excludeMsgIds);
                        List<String> envAppnameDiffList = new ArrayList<>();

                        // b、parse all registry-message-dto, by message-data
                        if (CollectionTool.isNotEmpty(messageList)) {
                            // filter registry-message
                            List<Message> registryMessageList = messageList.stream()
                                    .filter(item->item.getType()== MessageTypeEnum.REGISTRY.getValue())
                                    .collect(Collectors.toList());

                            // c、process each env-appname
                            if (CollectionTool.isNotEmpty(registryMessageList)) {
                                // convert to registry-message-dto（env-appname）
                                List<MessageForRegistryDTO> messageForRegistryDTOList = registryMessageList.stream()
                                        .map(item-> (JSON.parseObject(item.getData(), MessageForRegistryDTO.class))
                                        )
                                        .collect(Collectors.toList());

                                for (MessageForRegistryDTO messageForRegistryDTO: messageForRegistryDTOList) {
                                    // build key
                                    String envAppNameKey = buildCacheKey(messageForRegistryDTO.getEnv(), messageForRegistryDTO.getAppname());

                                    // build validValud
                                    List<InstanceCacheDTO> cacheValue = buildValidCache(messageForRegistryDTO.getEnv(), messageForRegistryDTO.getAppname());

                                    // build validValud-md5
                                    String cacheValueMd5 = Md5Tool.md5(JSON.toJSONString(cacheValue));

                                    // set data (key exists and not match)
                                    if (CollectionTool.isNotEmpty(cacheValue)) {
                                        if (!cacheValueMd5.equals(registryCacheMd5Store.get(envAppNameKey))) {
                                            registryCacheStore.put(envAppNameKey, cacheValue);
                                            registryCacheMd5Store.put(envAppNameKey, cacheValueMd5);      // only match md5, speed up match process

                                            // discovery diff
                                            envAppnameDiffList.add(envAppNameKey);
                                        }
                                    } else {
                                        logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread offline-pass envAppNameKey:{}", envAppNameKey);
                                    }
                                }
                            }

                            // avoid repeat message
                            readedMessageIds.addAll(messageList.stream().map(Message::getId).collect(Collectors.toList()));
                        }

                        // d、push client
                        if (CollectionTool.isNotEmpty(envAppnameDiffList)) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread find envAppnameDiffList:{}", envAppnameDiffList);
                            pushClient(envAppnameDiffList);
                        }

                        // e、clean old message， Avoid too often clean
                        if ( (System.currentTimeMillis()/1000) % REGISTRY_BEAT_TIME ==0) {
                            RegistryFactory.getInstance().getMessageMapper().cleanMessageInValid(msgTimeValidStart, msgTimeValidEnd);
                            readedMessageIds.clear();
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-messageListenThread stop");
            }
        }, "xxl-conf, admin RegistryCacheHelpler-messageListenThread");

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
        RegistryFactory.getInstance().getRegistryDeferredResultHelpler().pushClient(envAppnameDiffList);
        logger.info(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-pushClient, envAppnameDiffList:{}", envAppnameDiffList);
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
        stopThread(fullSyncThread);
        stopThread(messageListenThread);
    }


    // ---------------------- util ----------------------

    /**
     * start thread
     *
     * @param runnable
     * @param name
     * @return
     */
    public static Thread startThread(Runnable runnable, String name) {
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
    public static void stopThread(Thread thread) {
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

    // ---------------------- tool ----------------------

    /**
     * make cache key
     *
     * @param env
     * @param appname
     * @return
     */
    public static String buildCacheKey(String env, String appname){
        return String.format("%s##%s", env, appname);
    }

    /**
     * build valid cache
     *
     * @param env
     * @param appname
     * @return
     */
    private List<InstanceCacheDTO> buildValidCache(String env, String appname){
        // result
        List<InstanceCacheDTO> cacheValue = new ArrayList<>();

        // query valid instance
        Date registerHeartbeatValid = DateTool.addSeconds(new Date(), -1 * REGISTRY_BEAT_TIME * 3);
        List<Instance> instanceCacheDTOList = RegistryFactory.getInstance().getInstanceMapper().queryByEnvAndAppNameValid(
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
                    .map(InstanceCacheDTO::new)
                    .sorted(Comparator.comparing(InstanceCacheDTO::getSortKey))     // sort， for md5 match
                    .collect(Collectors.toList());
        }

        return cacheValue;
    }


    // ---------------------- helper ----------------------

    /**
     * find OnLine Instance
     *
     * @param env
     * @param appname
     * @return
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
     * @param env
     * @param appname
     * @return
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
     * @param request
     * @return
     */
    public DiscoveryResponse discoveryOnLineInstance(DiscoveryRequest request) {
        // valid
        if (request == null || CollectionTool.isEmpty(request.getAppnameList())) {
            return new DiscoveryResponse(OpenApiResponse.FAIL_CODE, "param Invalid.");
        }

        // query data
        Map<String, List<InstanceCacheDTO>> discoveryData = new HashMap<>();
        Map<String, String> discoveryDataMd5 = new HashMap<>();
        for (String appname : request.getAppnameList()) {
            String onLineInstanceMd5 = findOnLineInstanceMd5(request.getEnv(), appname);
            if (StringTool.isNotBlank(onLineInstanceMd5)) {
                // set md5
                discoveryDataMd5.put(appname, onLineInstanceMd5);
                // set detail
                if (!request.isSimpleQuery()) {
                    discoveryData.put(appname, findOnLineInstance(request.getEnv(), appname));
                }
            }
        }

        // build response
        DiscoveryResponse response = new DiscoveryResponse(SUCCESS_CODE, null);
        response.setDiscoveryData(discoveryData);
        response.setDiscoveryDataMd5(discoveryDataMd5);
        return response;
    }

}
