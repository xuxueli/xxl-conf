package com.xxl.conf.core.openapi.registry.model;

import java.io.Serializable;

/**
 *  Instance Cache DTO
 *
 *  Created by xuxueli on '2024-12-15 11:08:18'.
 */
public class InstanceCacheDTO implements Serializable {
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

    public InstanceCacheDTO() {
    }
    public InstanceCacheDTO(String env, String appname, String ip, int port, String extendInfo) {
        this.env = env;
        this.appname = appname;
        this.ip = ip;
        this.port = port;
        this.extendInfo = extendInfo;
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

    @Override
    public String toString() {
        return "InstanceCacheDTO{" +
                "env='" + env + '\'' +
                ", appname='" + appname + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", extendInfo='" + extendInfo + '\'' +
                '}';
    }

    // tool

    /**
     * get sort key
     * @return
     */
    public String getSortKey(){
        return ip + ":" + port;
    }

}