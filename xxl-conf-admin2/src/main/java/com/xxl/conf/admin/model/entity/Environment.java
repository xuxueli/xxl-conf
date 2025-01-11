package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
*  Environment Entity
*
*  Created by xuxueli on '2024-12-07 15:40:35'.
*/
public class Environment implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
    * 
    */
    private int id;

    /**
    * 环境标识
    */
    private String env;

    /**
    * 环境名称
    */
    private String name;

    /**
    * 环境描述
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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