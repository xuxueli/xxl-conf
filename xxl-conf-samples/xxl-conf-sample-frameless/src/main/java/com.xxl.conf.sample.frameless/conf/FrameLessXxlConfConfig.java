package com.xxl.conf.sample.frameless.conf;

import com.xxl.conf.core.factory.XxlConfBaseFactory;

/**
 * @author xuxueli 2018-10-31 19:05:43
 */
public class FrameLessXxlConfConfig {

    /**
     * init
     */
    public static void init() {
        XxlConfBaseFactory.init("xxl-conf.properties");
    }

    /**
     * destory
     */
    public static void destroy() {
        XxlConfBaseFactory.destroy();
    }

}
