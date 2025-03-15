package com.xxl.conf.admin.model.dto;

import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.tool.core.DateTool;

import java.io.Serializable;
import java.util.Date;

/**
 *  Instance Entity
 *
 *  Created by xuxueli on '2024-12-07 21:44:18'.
 */
public class InstanceDTO implements Serializable {
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
     * AppName（服务唯一标识）
     */
    private String appname;

    /**
     * 注册节点IP
     */
    private String ip;

    /**
     * 注册节点端口号
     */
    private int port;

    /**
     * 扩展信息
     */
    private String extendInfo;

    /**
     * 注册模式
     */
    private int registerModel;

    /**
     * 节点最后心跳时间，动态注册时判定是否过期
     */
    //private Date registerHeartbeat;

    /**
     * 新增时间
     */
    private Date addTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public InstanceDTO() {
    }
    public InstanceDTO(Instance instance) {
        this.id = instance.getId();
        this.env = instance.getEnv();
        this.appname = instance.getAppname();
        this.ip = instance.getIp();
        this.port = instance.getPort();
        this.extendInfo = instance.getExtendInfo();
        this.registerModel = instance.getRegisterModel();
        this.addTime = instance.getAddTime();
        this.updateTime = instance.getUpdateTime();

        if (instance.getRegisterHeartbeat() != null) {
            this.registerHeartbeat = DateTool.formatDateTime(instance.getRegisterHeartbeat());
        }
    }

    // add
    private String registerHeartbeat;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public int getRegisterModel() {
        return registerModel;
    }

    public void setRegisterModel(int registerModel) {
        this.registerModel = registerModel;
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

    public String getRegisterHeartbeat() {
        return registerHeartbeat;
    }

    public void setRegisterHeartbeat(String registerHeartbeat) {
        this.registerHeartbeat = registerHeartbeat;
    }
}