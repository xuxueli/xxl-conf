package com.xxl.conf.sample.frameless.conf;

import com.xxl.conf.core.factory.XxlConfBaseFactory;
import com.xxl.conf.core.util.PropUtil;

import java.util.Properties;

/**
 * @author xuxueli 2018-10-31 19:05:43
 */
public class FrameLessXxlConfConfig {

    /**
     * init
     */
    public static void init() {
        Properties prop = PropUtil.loadProp("xxl-conf.properties");

        XxlConfBaseFactory.init(
                prop.getProperty("xxl.conf.admin.address"),
                prop.getProperty("xxl.conf.env"),
                prop.getProperty("xxl.conf.access.token"),
                prop.getProperty("xxl.conf.mirrorfile"));
    }

    /**
     * destory
     */
    public static void destroy() {
        XxlConfBaseFactory.destroy();
    }

}
