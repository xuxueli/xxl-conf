package com.xxl.conf.admin.openapi.confdata.config;

import com.xxl.conf.admin.mapper.ConfDataMapper;
import com.xxl.conf.admin.openapi.confdata.thread.ConfDataCacheHelpler;
import com.xxl.conf.admin.openapi.confdata.thread.ConfDataDeferredResultHelpler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class ConfDataFactory implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(ConfDataFactory.class);

    // ---------------------- instance ----------------------
    private static ConfDataFactory instance;
    public static ConfDataFactory getInstance() {
        return instance;
    }


    // ---------------------- mapper ----------------------

    @Resource
    private ConfDataMapper confDataMapper;

    public ConfDataMapper getConfDataMapper() {
        return confDataMapper;
    }

    // ---------------------- helper ----------------------

    /**
     * 1、ConfDataCacheHelpler
     */
    private ConfDataCacheHelpler confDataCacheHelpler;

    /**
     * 2、ConfDataDeferredResultHelpler
     */
    private ConfDataDeferredResultHelpler confDataDeferredResultHelpler;

    public ConfDataCacheHelpler getConfDataCacheHelpler() {
        return confDataCacheHelpler;
    }

    public ConfDataDeferredResultHelpler getConfDataDeferredResultHelpler() {
        return confDataDeferredResultHelpler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // base init
        instance = this;

        // 1、RegistryCacheHelpler
        try {
            confDataCacheHelpler = new ConfDataCacheHelpler();
            confDataCacheHelpler.start();
        } catch (Throwable e) {
            logger.error("ConfDataFactory - ConfDataCacheHelpler: start error", e);
        }

        // 2、RegisterHelper
        try {
            confDataDeferredResultHelpler = new ConfDataDeferredResultHelpler();
            confDataDeferredResultHelpler.start();
        } catch (Throwable e) {
            logger.error("ConfDataFactory - ConfDataDeferredResultHelpler: start error", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        // 1、ConfDataCacheHelpler
        try {
            confDataCacheHelpler.stop();
        } catch (Throwable e) {
            logger.error("ConfDataFactory - ConfDataCacheHelpler: stop error", e);
        }

        // 2、ConfDataDeferredResultHelpler
        try {
            confDataDeferredResultHelpler.stop();
        } catch (Throwable e) {
            logger.error("ConfDataFactory - ConfDataDeferredResultHelpler: stop error", e);
        }
    }
}
