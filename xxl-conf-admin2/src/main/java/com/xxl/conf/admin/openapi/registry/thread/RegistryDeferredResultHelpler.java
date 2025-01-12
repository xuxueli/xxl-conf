package com.xxl.conf.admin.openapi.registry.thread;


import com.xxl.conf.admin.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.admin.openapi.registry.model.OpenApiResponse;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * registry DeferredResult helpler
 *
 * 功能：
 * 1、客户端连接保活功能：以instance 维护关注的 客户端监听器 集合；
 * 2、变更推动通道：接收到注册信息变更后，提供通道能力，通知客户端监听器、实时更新客户端数据；
 *
 * 面向：
 * 1、服务consumer：提供注册变更推送通道
 *
 * @author xuxueli
 */
public class RegistryDeferredResultHelpler {
    private static Logger logger = LoggerFactory.getLogger(RegistryDeferredResultHelpler.class);

    /**
     * 客户端监听器
     *
     * <pre>
     *     说明：以instance 维护关注的 客户端监听器 集合；
     *     Cache-Data：
     *          Key：String （与 RegistryCacheHelpler 缓存key保持一致）
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：List
     *              格式：DeferredResult，客户端注册
     * </pre>
     */
    private volatile Map<String, CopyOnWriteArrayList<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    /**
     * registry monitor (will remove instance that expired more than 1 day)
     */
    private Thread deferredResultMonitorThread;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;


    /**
     * start
     */
    public void start() {

        // deferredResultMonitorThread， for clean
        deferredResultMonitorThread = RegistryCacheHelpler.startThread(new Runnable() {
            @Override
            public void run() {
                logger.info(">>>>>>>>>>> xxl-conf, RegistryDeferredResultHelpler-deferredResultMonitorThread stop.");
                while (!toStop) {
                    try {
                        // clean dead DeferredResult
                        if (MapTool.isNotEmpty(registryDeferredResultMap)) {
                            for (Map.Entry<String, CopyOnWriteArrayList<DeferredResult>> entry : registryDeferredResultMap.entrySet()) {
                                String key = entry.getKey();
                                CopyOnWriteArrayList<DeferredResult> deferredResultList = entry.getValue();
                                if (CollectionTool.isNotEmpty(deferredResultList)) {
                                    List<DeferredResult> toRemove = deferredResultList.stream().filter(item->item.isSetOrExpired()).collect(Collectors.toList());
                                    deferredResultList.removeAll(toRemove);     // thread-safe write
                                } else {
                                    registryDeferredResultMap.remove(key);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryDeferredResultHelpler-deferredResultMonitorThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryCacheHelpler.REGISTRY_BEAT_TIME * 3);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryDeferredResultHelpler-deferredResultMonitorThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, RegistryDeferredResultHelpler-deferredResultMonitorThread finish.");
            }
        }, "xxl-conf, RegistryDeferredResultHelpler-deferredResultMonitorThread");

    }

    public void stop() {
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // TODO1，registryDeferredResultMap clean

        // stop thread
        RegistryCacheHelpler.stopThread(deferredResultMonitorThread);
    }

    // ---------------------- helper ----------------------

    /**
     * pushClient
     *
     * @param envAppnameList
     * @return
     */
    public void pushClient(List<String> envAppnameList){
        if (CollectionTool.isEmpty(envAppnameList)) {
            return;
        }

        // find client and push
        for (String envAppname: envAppnameList) {
            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(envAppname);
            if (CollectionTool.isNotEmpty(deferredResultList)) {
                registryDeferredResultMap.remove(envAppname);   // thread-safe write
                for (DeferredResult deferredResult: deferredResultList) {
                    deferredResult.setResult(new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, "Monitor key("+ envAppname +") update."));
                }
            }
        }
    }

    /**
     * monitor
     *
     * @param request
     * @return
     */
    public DeferredResult<OpenApiResponse> monitor(DiscoveryRequest request) {

        // init
        DeferredResult deferredResult = new DeferredResult(30 * 1000L, new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, "Monitor timeout, no key updated."));

        // valid
        if (request == null) {
            deferredResult.setResult(new OpenApiResponse(OpenApiResponse.FAIL_CODE, "request invalid"));
            return deferredResult;
        }


        // monitor by client
        for (String appname: request.getAppnameList()) {
            // monitor key, same as cache key
            String cacheKey = RegistryCacheHelpler.buildCacheKey(request.getEnv(), appname);
            registryDeferredResultMap
                    .computeIfAbsent(cacheKey, k -> new CopyOnWriteArrayList<>())       // thread-safe write, put list
                    .add(deferredResult);                                                     // thread-safe write, add list-data
        }

        return deferredResult;
    }

}
