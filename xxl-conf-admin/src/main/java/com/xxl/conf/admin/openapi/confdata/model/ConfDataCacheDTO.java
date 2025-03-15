package com.xxl.conf.admin.openapi.confdata.model;

import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.tool.encrypt.Md5Tool;

import java.io.Serializable;

/**
 *  ConfData Cache DTO
 *
 *  Created by xuxueli
 */
public class ConfDataCacheDTO implements Serializable {
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
     * 配置项Key
     */
    private String key;

    /**
     * 配置项Value
     */
    private String value;

    // md5
    private String valueMd5;

    public ConfDataCacheDTO() {
    }
    public ConfDataCacheDTO(String env, String appname, String key, String value) {
        this.env = env;
        this.appname = appname;
        this.key = key;
        this.value = value;

        // value md5
        this.valueMd5 = Md5Tool.md5(value);
    }

    public ConfDataCacheDTO(ConfData confData) {
        this.env = confData.getEnv();
        this.appname = confData.getAppname();
        this.key = confData.getKey();
        this.value = confData.getValue();

        // value md5
        this.valueMd5 = Md5Tool.md5(value);
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueMd5() {
        return valueMd5;
    }

    public void setValueMd5(String valueMd5) {
        this.valueMd5 = valueMd5;
    }

    @Override
    public String toString() {
        return "ConfDataCacheDTO{" +
                "env='" + env + '\'' +
                ", appname='" + appname + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", valueMd5='" + valueMd5 + '\'' +
                '}';
    }

    public String getSortKey(){
        return env + "#" + appname + "#" + key;
    }

}