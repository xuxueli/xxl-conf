package com.xxl.conf.admin.model.dto;

import java.io.Serializable;
import java.util.Date;

/**
*  ConfDataLog Entity
*
*  Created by xuxueli on '2025-01-11 23:08:28'.
*/
public class ConfDataLogDTO implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
    * id
    */
    private long id;

    /**
    * 配置数据ID
    */
    private long dataId;

    /**
    * 历史数据，配置项Value
    */
    private String value;

    /**
    * 操作人，账号
    */
    private String optUsername;

    /**
    * 新增时间
    */
    private String addTime;

    /**
    * 更新时间
    */
    private String updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOptUsername() {
        return optUsername;
    }

    public void setOptUsername(String optUsername) {
        this.optUsername = optUsername;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}