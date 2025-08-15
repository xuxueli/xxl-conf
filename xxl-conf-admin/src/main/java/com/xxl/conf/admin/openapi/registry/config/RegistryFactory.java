package com.xxl.conf.admin.openapi.registry.config;

import com.xxl.conf.admin.mapper.*;
import com.xxl.conf.admin.openapi.registry.thread.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * registry config
 *
 * @author xuxueli
 */
@Configuration
public class RegistryFactory implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(RegistryFactory.class);

    // ---------------------- instance ----------------------
    private static RegistryFactory instance;
    public static RegistryFactory getInstance() {
        return instance;
    }

    // ---------------------- helper ----------------------
    @Resource
    private InstanceMapper instanceMapper;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private EnvironmentMapper environmentMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private AccessTokenMapper accessTokenMapper;

    public InstanceMapper getInstanceMapper() {
        return instanceMapper;
    }
    public ApplicationMapper getApplicationMapper() {
        return applicationMapper;
    }
    public EnvironmentMapper getEnvironmentMapper() {
        return environmentMapper;
    }
    public MessageMapper getMessageMapper() {
        return messageMapper;
    }
    public AccessTokenMapper getAccessTokenMapper() {
        return accessTokenMapper;
    }

    // ---------------------- helper ----------------------

    /**
     * 1、RegistryCacheHelpler
     */
    private RegistryCacheHelpler registryCacheHelpler;

    /**
     * 2、MessageHeader
     */
    private MessageHelpler messageHeader;

    /**
     * 3、RegisterHelper
     */
    private RegisterHelper registerHelper;

    /**
     * 4、RegistryDeferredResultHelpler
     */
    private RegistryDeferredResultHelpler registryDeferredResultHelpler;

    /**
     * 5、AccessTokenHelpler
     */
    private AccessTokenHelpler accessTokenHelpler;


    public RegistryCacheHelpler getRegistryCacheHelpler() {
        return registryCacheHelpler;
    }

    public RegisterHelper getRegisterHelper() {
        return registerHelper;
    }

    public RegistryDeferredResultHelpler getRegistryDeferredResultHelpler() {
        return registryDeferredResultHelpler;
    }

    public AccessTokenHelpler getAccessTokenHelpler() {
        return accessTokenHelpler;
    }

    public MessageHelpler getMessageHeader() {
        return messageHeader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // base init
        instance = this;

        // 1、RegistryCacheHelpler
        try {
            registryCacheHelpler = new RegistryCacheHelpler();
            registryCacheHelpler.start();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registryCacheHelpler: start error", e);
        }

        // 2、MessageHelpler
        try {
            messageHeader = new MessageHelpler();
            messageHeader.start();
        } catch (Throwable e) {
            logger.error("RegistryFactory - messageHeader: start error", e);
        }

        // 3、RegisterHelper
        try {
            registerHelper = new RegisterHelper();
            registerHelper.start();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registerHelper: start error", e);
        }

        // 4、RegistryDeferredResultHelpler
        try {
            registryDeferredResultHelpler = new RegistryDeferredResultHelpler();
            registryDeferredResultHelpler.start();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registryDeferredResultHelpler: start error", e);
        }

        // 5、AccessTokenHelpler
        try {
            accessTokenHelpler = new AccessTokenHelpler();
            accessTokenHelpler.start();
        } catch (Throwable e) {
            logger.error("RegistryFactory - accessTokenHelpler: start error", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        // 1、RegistryCacheHelpler
        try {
            registryCacheHelpler.stop();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registryCacheHelpler: stop error", e);
        }

        // 2、MessageHeader
        try {
            messageHeader.stop();
        } catch (Throwable e) {
            logger.error("RegistryFactory - messageHeader: stop error", e);
        }

        // 3、RegisterHelper
        try {
            registerHelper.stop();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registerHelper: stop error", e);
        }

        // 4、RegistryDeferredResultHelpler
        try {
            registryDeferredResultHelpler.stop();
        } catch (Throwable e) {
            logger.error("RegistryFactory - registryDeferredResultHelpler: stop error", e);
        }

        // 5、AccessTokenHelpler
        try {
            accessTokenHelpler.stop();
        } catch (Throwable e) {
            logger.error("RegistryFactory - accessTokenHelpler: stop error", e);
        }

    }

}
