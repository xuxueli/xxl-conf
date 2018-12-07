package com.xxl.conf.core.model;

import java.util.List;

/**
 * @author xuxueli 2018-12-07
 */
public class XxlConfParamVO {

    private String accessToken;
    private String env;
    private List<String> keys;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

}
