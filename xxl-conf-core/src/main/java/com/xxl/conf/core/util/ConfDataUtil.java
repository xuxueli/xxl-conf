package com.xxl.conf.core.util;

public class ConfDataUtil {

    /**
     * build env-appname
     *
     * @param env env
     * @param appname appname
     * @return key of cache
     */
    public static String buildEnvAppname(String env, String appname){
        return String.format("%s##%s", env, appname);
    }

}
