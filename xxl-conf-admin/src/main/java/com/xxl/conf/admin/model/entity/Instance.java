package com.xxl.conf.admin.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *  Instance Entity
 *
 *  Created by xuxueli on '2024-12-07 21:44:18'.
 */
public class Instance implements Serializable {
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
    private Date registerHeartbeat;

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

    public Date getRegisterHeartbeat() {
        return registerHeartbeat;
    }

    public void setRegisterHeartbeat(Date registerHeartbeat) {
        this.registerHeartbeat = registerHeartbeat;
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

    @Override
    public String toString() {
        return "Instance{" +
                "id=" + id +
                ", env='" + env + '\'' +
                ", appname='" + appname + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", extendInfo='" + extendInfo + '\'' +
                ", registerModel=" + registerModel +
                ", registerHeartbeat=" + registerHeartbeat +
                ", addTime=" + addTime +
                ", updateTime=" + updateTime +
                '}';
    }

}