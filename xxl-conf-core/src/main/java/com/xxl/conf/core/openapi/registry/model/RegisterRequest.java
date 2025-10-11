package com.xxl.conf.core.openapi.registry.model;

import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class RegisterRequest implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * client instance
     */
    private RegisterInstance instance;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public RegisterInstance getInstance() {
        return instance;
    }

    public void setInstance(RegisterInstance instance) {
        this.instance = instance;
    }

}
