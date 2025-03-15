package com.xxl.conf.admin.model.dto;

import com.xxl.conf.admin.model.entity.ConfData;

import java.io.Serializable;

/**
*  Message CongData DTO
*
*  Created by xuxueli
*/
public class MessageForConfDataDTO implements Serializable {
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
     * 配置key
     */
    private String key;

    public MessageForConfDataDTO() {
    }
    public MessageForConfDataDTO(ConfData confData) {
        this.env = confData.getEnv();
        this.appname = confData.getAppname();
        this.key = confData.getKey();
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}