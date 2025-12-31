package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
*  ConfDataLog Entity
*
*  Created by xuxueli on '2025-01-11 23:08:28'.
*/
public class ConfDataLog implements Serializable {
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
     * 变更前，配置项Value
     */
    private String oldValue;

    /**
    * 变更后，配置项Value
    */
    private String value;

    /**
    * 操作人，账号
    */
    private String optUsername;

    /**
    * 新增时间
    */
    private Date addTime;

    /**
    * 更新时间
    */
    private Date updateTime;

    public ConfDataLog() {
    }
    public ConfDataLog(long dataId, String oldValue, String value, String optUsername) {
        this.dataId = dataId;
        this.oldValue = oldValue;
        this.value = value;
        this.optUsername = optUsername;
    }

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

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
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