package com.xxl.conf.admin.openapi.confdata.thread;

import com.xxl.conf.core.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.conf.core.openapi.confdata.model.MonitorRequest;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private static final Logger logger = LoggerFactory.getLogger(ConfDataDeferredResultHelpler.class);

    /**
     * 客户端监听器
     *
     *  1、数据结构：
     * <pre>
     *     {
     *         "test##app01": [                         // key  ： "{Env}##{Appname}"        // 与 RegistryCacheHelpler 缓存key保持一致
     *              DeferredResult,                     // value： List<DeferredResult>      // 监听客户端，延迟结果
     *              DeferredResult
     *         ]
     *     }
     * </pre>
     *
     *  2、说明：appname 维度，管理 客户端监听器 集合；
     */
    private final Map<String, CopyOnWriteArrayList<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    /**
     * registry monitor
     *
     *  1、remove instance that expired more than 24 hours
     */
    private CyclicThread deferredResultMonitorThread;

    /**
     * start
     */
    public void start() {
        deferredResultMonitorThread  = new CyclicThread("ConfDataDeferredResultHelpler-deferredResultMonitorThread", true, new Runnable() {
            @Override
            public void run() {

                // clean dead DeferredResult
                if (MapTool.isNotEmpty(registryDeferredResultMap)) {
                    for (Map.Entry<String, CopyOnWriteArrayList<DeferredResult>> entry : registryDeferredResultMap.entrySet()) {
                        // entry
                        String key = entry.getKey();
                        CopyOnWriteArrayList<DeferredResult> deferredResultList = entry.getValue();

                        // remove dead DeferredResult
                        if (CollectionTool.isNotEmpty(deferredResultList)) {
                            List<DeferredResult> toRemove = deferredResultList.stream().filter(item->item.isSetOrExpired()).collect(Collectors.toList());
                            deferredResultList.removeAll(toRemove);     // thread-safe write
                        } else {
                            registryDeferredResultMap.remove(key);
                        }
                    }
                }

            }
        }, ConfDataCacheHelpler.REFRESH_INTERVAL * 3 * 1000, true);
        deferredResultMonitorThread.start();

    }

    public void stop() {
        // deferredResultMonitorThread
        if (deferredResultMonitorThread != null) {
            deferredResultMonitorThread.stop();
        }
    }

    // ---------------------- helper ----------------------

    /**
     * make cache key
     */
    public static String buildMonitorKey(String env, String appname){
        return String.format("%s##%s", env, appname);
    }

    /**
     * pushClient
     */
    public void pushClient(List<ConfDataCacheDTO> diffConfList){
        if (CollectionTool.isEmpty(diffConfList)) {
            return;
        }

        // find client and push
        for (ConfDataCacheDTO confDataCacheDTO: diffConfList) {
            // build param
            String envAppnameKey = buildMonitorKey(confDataCacheDTO.getEnv(), confDataCacheDTO.getAppname());
            String failMsg = "Monitor key update: env="+confDataCacheDTO.getEnv()+", appname="+ confDataCacheDTO.getAppname() +", key="+confDataCacheDTO.getKey();

            // do push
            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(envAppnameKey);
            if (CollectionTool.isNotEmpty(deferredResultList)) {
                registryDeferredResultMap.remove(envAppnameKey);   // thread-safe write
                for (DeferredResult deferredResult: deferredResultList) {
                    try {
                        deferredResult.setResult(Response.ofFail(failMsg));
                    } catch (Exception e) {
                        logger.error(">>>>>>>>>>> xxl-conf, ConfDataDeferredResultHelpler-pushClient error, failResponse:{}", failMsg, e);
                    }
                }
            }
        }
    }

    /**
     * monitor
     */
    public DeferredResult<Response<String>> monitor(MonitorRequest request) {

        // init
        DeferredResult<Response<String>> deferredResult = new DeferredResult<>(
                30 * 1000L,
                Response.ofSuccess("Monitor timeout, no key updated."));

        // valid
        if (request == null || CollectionTool.isEmpty(request.getAppnameList())) {
            deferredResult.setResult(Response.ofFail("request invalid"));
            return deferredResult;
        }

        // monitor by client
        for (String appname: request.getAppnameList()) {

            // monitor key
            String envAppnameKey = buildMonitorKey(request.getEnv(), appname);

            // registry monitor
            registryDeferredResultMap
                    .computeIfAbsent(envAppnameKey, k -> new CopyOnWriteArrayList<>())       // thread-safe write, put list
                    .add(deferredResult);                                                           // thread-safe write, add list-data
        }

        return deferredResult;
    }

}
