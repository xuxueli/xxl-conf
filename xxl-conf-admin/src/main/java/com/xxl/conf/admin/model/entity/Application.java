package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
*  Application Entity
*
*  Created by xuxueli on '2024-12-07 16:54:11'.
*/
public class Application implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
    * id
    */
    private int id;

    /**
    * 应用唯一标识
    */
    private String appname;

    /**
    * 应用名称
    */
    private String name;

    /**
    * 应用描述
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

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
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