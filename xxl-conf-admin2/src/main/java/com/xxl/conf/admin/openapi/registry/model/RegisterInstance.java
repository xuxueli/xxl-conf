package com.xxl.conf.admin.openapi.registry.model;

import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class RegisterInstance implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * AppName（应用唯一标识）
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
     * 扩展信息（可选）
     */
    private String extendInfo;

    public RegisterInstance() {
    }
    public RegisterInstance(String appname, String ip, int port, String extendInfo) {
        this.appname = appname;
        this.ip = ip;
        this.port = port;
        this.extendInfo = extendInfo;
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

}
