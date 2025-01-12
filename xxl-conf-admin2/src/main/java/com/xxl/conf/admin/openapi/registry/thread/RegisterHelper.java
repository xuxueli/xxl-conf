package com.xxl.conf.admin.openapi.registry.thread;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.model.entity.Message;
import com.xxl.conf.admin.openapi.registry.config.RegistryFactory;
import com.xxl.conf.admin.openapi.common.model.OpenApiResponse;
import com.xxl.conf.admin.openapi.registry.model.RegisterRequest;
import com.xxl.tool.core.StringTool;
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
    private static Logger logger = LoggerFactory.getLogger(RegisterHelper.class);

    /**
     * Expired To Clean Interval, by hour
     */
    private static final int EXPIRED_TO_CLEAN_TIME = 24;

    /**
     * register or unregister
     */
    private ThreadPoolExecutor registerOrUnregisterThreadPool = null;

    /**
     * registry monitor (will remove instance that expired more than 1 day)
     */
    private Thread registryMonitorThread;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;


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

        // 2、registryMonitorThread， for registry clean
        registryMonitorThread = RegistryCacheHelpler.startThread(new Runnable() {
            @Override
            public void run() {
                logger.info(">>>>>>>>>>> xxl-conf, RegistryHelpler-registryMonitorThread start");
                while (!toStop) {
                    try {
                        // TODO, clean dead instance

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        //
                        TimeUnit.HOURS.sleep(EXPIRED_TO_CLEAN_TIME);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, RegistryHelpler-registryMonitorThread stop");
            }
        }, "xxl-conf, admin RegistryCacheHelpler-messageListenThread");

    }

    /**
     * stop
     */
    public void stop() {
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 1、stop registryOrRemoveThreadPool
        try {
            registerOrUnregisterThreadPool.shutdownNow();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 2、registryMonitorThread
        RegistryCacheHelpler.stopThread(registryMonitorThread);
    }

    // ---------------------- helper ----------------------

    /**
     * registry
     *
     * @param request
     * @return
     */
    public OpenApiResponse registry(RegisterRequest request) {
        // valid
        if (request == null) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest is null.");
        }
        if (StringTool.isBlank(request.getEnv())
                || request.getInstance() == null
                || StringTool.isBlank(request.getInstance().getAppname())
                || StringTool.isBlank(request.getInstance().getIp())
                || request.getInstance().getPort()<1){
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest param invalid.");
        }
        if (request.getInstance().getAppname().length() > 50) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest param invalid, appname too long (less than 50).");
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

                int ret = RegistryFactory.getInstance().getInstanceMapper().addAutoInstance(instance);
                if (ret > 0) {
                    // message
                    Message message = new Message();
                    message.setType(MessageTypeEnum.REGISTRY.getValue());
                    message.setData(JSON.toJSONString(new MessageForRegistryDTO(instance)));      // convert
                    message.setAddTime(new Date());
                    message.setUpdateTime(new Date());
                    RegistryFactory.getInstance().getMessageMapper().insert(message);
                }
            }
        });

        return new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, null);
    }

    /**
     * unregister
     *
     * @param request
     * @return
     */
    public OpenApiResponse unregister(RegisterRequest request) {
        // valid
        if (request == null) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest is null.");
        }
        if (StringTool.isBlank(request.getEnv())
                || request.getInstance() == null
                || StringTool.isBlank(request.getInstance().getAppname())
                || StringTool.isBlank(request.getInstance().getIp())
                || request.getInstance().getPort()<1){
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest param invalid.");
        }
        if (request.getInstance().getAppname().length() > 50) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "RegisterRequest param invalid, appname too long (less than 50).");
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

                int ret = RegistryFactory.getInstance().getInstanceMapper().deleteAutoInstance(instance);
                if (ret > 0) {
                    // message
                    Message message = new Message();
                    message.setType(MessageTypeEnum.REGISTRY.getValue());
                    message.setData(JSON.toJSONString(new MessageForRegistryDTO(instance)));      // convert
                    message.setAddTime(new Date());
                    message.setUpdateTime(new Date());
                    RegistryFactory.getInstance().getMessageMapper().insert(message);
                }
            }
        });

        return new OpenApiResponse(OpenApiResponse.SUCCESS_CODE, null);
    }

}
