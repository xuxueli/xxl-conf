package com.xxl.conf.admin.openapi.registry.model;


import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class OpenApiRequest implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * Env
     */
    private String env;

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

}
