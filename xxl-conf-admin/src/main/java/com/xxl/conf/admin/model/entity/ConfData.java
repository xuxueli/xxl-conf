package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
*  ConfData Entity
*
*  Created by xuxueli on '2025-01-11 23:01:14'.
*/
public class ConfData implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
    * id
    */
    private long id;

    /**
    * Env（环境唯一标识）
    */
    private String env;

    /**
    * AppName（应用唯一标识）
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

    /**
    * 配置项描述
    */
    private String desc;

    /**
    * 新增时间
    */
    private Date addTime;

    /**
    * 更新时间
    */
    private Date updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}