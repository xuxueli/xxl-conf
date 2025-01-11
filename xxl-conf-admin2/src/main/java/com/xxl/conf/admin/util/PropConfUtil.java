package com.xxl.conf.admin.util;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class PropConfUtil implements InitializingBean {

    private static PropConfUtil single = null;
    public static PropConfUtil getSingle() {
        return single;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        single = this;
    }

    // conf
    @Value("${xxl.conf.i18n}")
    private String i18n;

    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }


}
