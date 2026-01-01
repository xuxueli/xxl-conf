package com.xxl.conf.sample.config;

import com.xxl.conf.core.bootstrap.support.SpringXxlConfBootstrap;
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
    private static final Logger logger = LoggerFactory.getLogger(XxlConfConfig.class);

    @Value("${xxl.conf.admin.address}")
    private String address;

    @Value("${xxl.conf.admin.accesstoken}")
    private String accesstoken;

    @Value("${xxl.conf.client.appname}")
    private String appname;

    @Value("${xxl.conf.client.env}")
    private String env;

    @Value("${xxl.conf.client.filepath}")
    private String filepath;

    @Bean
    public SpringXxlConfBootstrap xxlConfBootstrap() {

        SpringXxlConfBootstrap xxlConfBootstrap = new SpringXxlConfBootstrap();
        xxlConfBootstrap.setAppname(appname);
        xxlConfBootstrap.setEnv(env);
        xxlConfBootstrap.setAddress(address);
        xxlConfBootstrap.setAccesstoken(accesstoken);
        xxlConfBootstrap.setFilepath(filepath);

        return xxlConfBootstrap;
    }

}