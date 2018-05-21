package com.xxl.conf.sample.config;

import com.xxl.conf.core.spring.XxlConfFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-conf config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlConfConfig {
    private Logger logger = LoggerFactory.getLogger(XxlConfConfig.class);

    @Bean
    public XxlConfFactory xxlConfFactory() {
        XxlConfFactory xxlConf = new XxlConfFactory();
        logger.info(">>>>>>>>>>> xxl-conf config init.");
        return xxlConf;
    }

}