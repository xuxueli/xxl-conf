package com.xxl.conf.sample.config;

import com.xxl.conf.core.spring.XxlConfFactory;
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


    @Value("${xxl.conf.zkaddress}")
    private String zkaddress;

    @Value("${xxl.conf.zkdigest}")
    private String zkdigest;

    @Value("${xxl.conf.env}")
    private String env;

    @Value("${xxl.conf.mirrorfile}")
    private String mirrorfile;


    @Bean
    public XxlConfFactory xxlConfFactory() {

        XxlConfFactory xxlConf = new XxlConfFactory();
        xxlConf.setZkaddress(zkaddress);
        xxlConf.setZkdigest(zkdigest);
        xxlConf.setEnv(env);
        xxlConf.setMirrorfile(mirrorfile);

        logger.info(">>>>>>>>>>> xxl-conf config init.");
        return xxlConf;
    }

}