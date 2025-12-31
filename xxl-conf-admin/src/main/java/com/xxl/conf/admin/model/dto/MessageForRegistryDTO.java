package com.xxl.conf.admin.model.dto;

import com.xxl.conf.admin.model.entity.Instance;

import java.io.Serializable;

/**
*  Message Registry DTO
*
*  Created by xuxueli on '2024-12-15 23:10:24'.
*/
public class MessageForRegistryDTO implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * Env（环境唯一标识）
     */
    private String env;

    /**
     * AppName（服务唯一标识）
     */
    private String appname;

    /**
     * id
     */
    private long instanceId;

    public MessageForRegistryDTO() {
    }
    public MessageForRegistryDTO(Instance instance) {
        this.env = instance.getEnv();
        this.appname = instance.getAppname();
        this.instanceId = instance.getId();
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

}