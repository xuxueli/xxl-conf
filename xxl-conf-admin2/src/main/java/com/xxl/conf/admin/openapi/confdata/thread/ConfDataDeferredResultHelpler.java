package com.xxl.conf.admin.openapi.confdata.thread;


import com.xxl.conf.admin.openapi.common.model.OpenApiResponse;
import com.xxl.conf.admin.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataRequest;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataResponse;
import com.xxl.conf.admin.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.admin.openapi.registry.thread.RegistryCacheHelpler;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * confData DeferredResult helpler
 *
 * 功能：
 * 1、客户端连接保活功能：以 appname 维护关注的 客户端监听器 集合；
 * 2、变更推动通道：接收到信息变更后，提供通道能力，通知客户端监听器、实时更新客户端数据；
 *
 * 面向：
 * 1、服务consumer：提供注册变更推送通道
 *
 * @author xuxueli
 */
public class ConfDataDeferredResultHelpler {
    private static Logger logger = LoggerFactory.getLogger(ConfDataDeferredResultHelpler.class);

    /**
     * 客户端监听器
     *
     * <pre>
     *     说明：以 appname 维护关注的 客户端监听器 集合；
     *     Cache-Data：
     *          Key：String
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
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataDeferredResultHelpler-deferredResultMonitorThread stop.");
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
                            logger.error(">>>>>>>>>>> xxl-conf, ConfDataDeferredResultHelpler-deferredResultMonitorThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(ConfDataCacheHelpler.REFRESH_INTERVAL * 3);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, ConfDataDeferredResultHelpler-deferredResultMonitorThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, ConfDataDeferredResultHelpler-deferredResultMonitorThread finish.");
            }
        }, "xxl-conf, ConfDataDeferredResultHelpler-deferredResultMonitorThread");

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
     * build monitor key
     */
    public static String buildMonitorKey(String env, String appname){
        return String.format("%s##%s", env, appname);
    }

    /**
     * pushClient
     *
     * @param diffConfList
     * @return
     */
    public void pushClient(List<ConfDataCacheDTO> diffConfList){
        if (CollectionTool.isEmpty(diffConfList)) {
            return;
        }

        // find client and push
        for (ConfDataCacheDTO confDataCacheDTO: diffConfList) {
            // monitor key
            String envAppnameKey = buildMonitorKey(confDataCacheDTO.getEnv(), confDataCacheDTO.getAppname());
            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(envAppnameKey);
            if (CollectionTool.isNotEmpty(deferredResultList)) {
                registryDeferredResultMap.remove(confDataCacheDTO);   // thread-safe write
                for (DeferredResult deferredResult: deferredResultList) {
                    deferredResult.setResult(new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, "Monitor key("+ confDataCacheDTO +") update."));
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
    public DeferredResult<QueryConfDataResponse> monitor(QueryConfDataRequest request) {

        // init
        DeferredResult deferredResult = new DeferredResult(30 * 1000L, new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, "Monitor timeout, no key updated."));

        // valid
        if (request == null || request.getConfKey()==null) {
            deferredResult.setResult(new OpenApiResponse(OpenApiResponse.FAIL_CODE, "request invalid"));
            return deferredResult;
        }

        // monitor by client
        for (String appname: request.getConfKey().keySet()) {
            // monitor key
            String envAppnameKey = buildMonitorKey(request.getEnv(), appname);
            registryDeferredResultMap
                    .computeIfAbsent(envAppnameKey, k -> new CopyOnWriteArrayList<>())       // thread-safe write, put list
                    .add(deferredResult);                                                           // thread-safe write, add list-data
        }

        return deferredResult;
    }

}
