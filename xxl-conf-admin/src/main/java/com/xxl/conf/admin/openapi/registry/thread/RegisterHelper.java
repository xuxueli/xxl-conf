package com.xxl.conf.admin.openapi.registry.thread;

import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.conf.core.openapi.registry.model.RegisterRequest;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Register Helper
 *
 * 功能：
 * 1、服务注册/注销能力：借助线程队列异步处理，批量写入注册 / 注销数据；
 * 2、注册变更，全局广播广播触发：写入后检测“DB与缓存”是否一致，若不一致发送 “注册广播Message”（触发 RegistryCacheHelpler 实时更新缓存）
 * 3、注册信息维护：主动清理长期过期注册信息（超过24H）；
 *
 * 面向：
 * 1、服务provider：提供 注册/注销 能力
 *
 * @author xuxueli
 */
public class RegisterHelper {
    private static final Logger logger = LoggerFactory.getLogger(RegisterHelper.class);

    /**
     * Expired To Clean Interval, by hour
     */
    private static final int EXPIRED_TO_CLEAN_TIME = 24;

    /**
     * register or unregister
     */
    private ThreadPoolExecutor registerOrUnregisterThreadPool = null;

    /**
     * registry cleanup threadmonitor (will remove instance that expired more than 1 day)
     */
    private CyclicThread registryCleanupThread;


    /**
     * start
     */
    public void start() {

        // 1、registerOrUnregisterThreadPool， for registry or unregister
        registerOrUnregisterThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-conf, RegisterHelper-registerOrUnregisterThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        logger.warn(">>>>>>>>>>> xxl-conf, RegisterHelper-registerOrUnregisterThreadPool, registry or unregister too fast, match threadpool rejected handler.");
                    }
                });

        // 2、registryCleanupThread， for registry clean
        registryCleanupThread = new CyclicThread("RegisterHelper-registryCleanupThread", true, new Runnable() {
            @Override
            public void run() {

                // delete expired auto instance, expired for 24 hours
                Date registerHeartbeat = DateTool.addHours(new Date(), -EXPIRED_TO_CLEAN_TIME);
                int ret = RegistryBootstrap.getInstance().getInstanceMapper().deleteExpiredAutoInstance(InstanceRegisterModelEnum.AUTO.getValue(), registerHeartbeat);
                if (ret > 0) {
                    logger.info(">>>>>>>>>>> xxl-conf, RegisterHelper-registryCleanupThread deleteExpiredAutoInstance count:{}", ret);
                }
                logger.debug(">>>>>>>>>>> xxl-conf, RegisterHelper-registryCleanupThread deleteExpiredAutoInstance count:{}", ret);

            }
        }, EXPIRED_TO_CLEAN_TIME * 60 * 60 * 1000, true);
        registryCleanupThread.start();

    }

    /**
     * stop
     */
    public void stop() {

        // 1、stop registryOrRemoveThreadPool
        try {
            registerOrUnregisterThreadPool.shutdownNow();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 2、stop registryMonitorThread
        if (registryCleanupThread != null) {
            registryCleanupThread.stop();
        }
    }

    // ---------------------- helper ----------------------

    /**
     * registry
     *
     * @param request request
     * @return  response
     */
    public Response<String> registry(RegisterRequest request) {
        // valid
        if (request == null) {
            return Response.ofFail("RegisterRequest is null.");
        }
        if (StringTool.isBlank(request.getEnv())
                || request.getInstance() == null
                || StringTool.isBlank(request.getInstance().getAppname())
                || StringTool.isBlank(request.getInstance().getIp())
                || request.getInstance().getPort()<1){
            return Response.ofFail("RegisterRequest param invalid.");
        }
        if (request.getInstance().getAppname().length() > 50) {
            return Response.ofFail("RegisterRequest param invalid, appname too long (less than 50).");
        }

        // async execute
        registerOrUnregisterThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // save
                Instance instance = new Instance();
                instance.setEnv(request.getEnv());
                instance.setAppname(request.getInstance().getAppname());
                instance.setIp(request.getInstance().getIp());
                instance.setPort(request.getInstance().getPort());
                instance.setExtendInfo(request.getInstance().getExtendInfo());
                instance.setRegisterModel(InstanceRegisterModelEnum.AUTO.getValue());
                instance.setRegisterHeartbeat(new Date());

                int ret = RegistryBootstrap.getInstance().getInstanceMapper().addAutoInstance(instance);
                if (ret > 0) {
                    // broadcast message
                    MessageHelpler.broadcastMessage(MessageTypeEnum.REGISTRY, GsonTool.toJson(new MessageForRegistryDTO(instance)));
                }
            }
        });

        return Response.ofSuccess();
    }

    /**
     * unregister
     *
     * @param request request
     * @return response
     */
    public Response<String> unregister(RegisterRequest request) {
        // valid
        if (request == null) {
            return Response.ofFail("RegisterRequest is null.");
        }
        if (StringTool.isBlank(request.getEnv())
                || request.getInstance() == null
                || StringTool.isBlank(request.getInstance().getAppname())
                || StringTool.isBlank(request.getInstance().getIp())
                || request.getInstance().getPort()<1){
            return Response.ofFail("RegisterRequest param invalid.");
        }
        if (request.getInstance().getAppname().length() > 50) {
            return Response.ofFail("RegisterRequest param invalid, appname too long (less than 50).");
        }

        // async execute
        registerOrUnregisterThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // delete
                Instance instance = new Instance();
                instance.setEnv(request.getEnv());
                instance.setAppname(request.getInstance().getAppname());
                instance.setIp(request.getInstance().getIp());
                instance.setPort(request.getInstance().getPort());
                instance.setRegisterModel(InstanceRegisterModelEnum.AUTO.getValue());

                int ret = RegistryBootstrap.getInstance().getInstanceMapper().deleteAutoInstance(instance);
                if (ret > 0) {
                    // broadcast message
                    MessageHelpler.broadcastMessage(MessageTypeEnum.REGISTRY, GsonTool.toJson(new MessageForRegistryDTO(instance)));
                }
            }
        });

        return Response.ofSuccess();
    }

}
