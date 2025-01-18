package com.xxl.conf.core.listener;

/**
 * xxl conf listener
 *
 * @author xuxueli 2018-02-04 01:27:30
 */
public interface XxlConfListener {

    /**
     * invoke when first-use or conf-change
     */
    public void onChange(String appname, String key, String value) throws Exception;

}
