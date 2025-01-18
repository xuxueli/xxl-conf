package com.xxl.conf.sample.config;

import com.xxl.conf.core.factory.support.SpringXxlConfFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${xxl.conf.client.appname}")
    private String appname;

    @Value("${xxl.conf.client.env}")
    private String env;

    @Value("${xxl.conf.admin.address}")
    private String address;

    @Value("${xxl.conf.admin.accesstoken}")
    private String accesstoken;

    @Bean
    public SpringXxlConfFactory xxlConfFactory() {

        SpringXxlConfFactory xxlConfFactory = new SpringXxlConfFactory();
        xxlConfFactory.setAppname(appname);
        xxlConfFactory.setEnv(env);
        xxlConfFactory.setAddress(address);
        xxlConfFactory.setAccesstoken(accesstoken);

        return xxlConfFactory;
    }

}