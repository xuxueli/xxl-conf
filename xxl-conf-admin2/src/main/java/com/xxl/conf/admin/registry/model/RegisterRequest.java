package com.xxl.conf.admin.registry.model;

import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class RegisterRequest extends OpenApiRequest implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * client instance
     */
    private RegisterInstance instance;

    public RegisterInstance getInstance() {
        return instance;
    }

    public void setInstance(RegisterInstance instance) {
        this.instance = instance;
    }

}
