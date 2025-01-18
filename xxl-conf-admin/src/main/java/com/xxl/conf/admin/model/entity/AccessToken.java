package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
*  AccessToken Entity
*
*  Created by xuxueli on '2024-12-08 16:22:29'.
*/
public class AccessToken implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
    * id
    */
    private long id;

    /**
    * 注册发现AccessToken
    */
    private String accessToken;

    /**
    * 状态
    */
    private int status;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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