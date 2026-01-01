package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.List;

/**
 * Monitor Request
 *
 * @author xuxueli 2025-01-12
 */
public class MonitorRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * Appname list
     */
    private List<String> appnameList;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public List<String> getAppnameList() {
        return appnameList;
    }

    public void setAppnameList(List<String> appnameList) {
        this.appnameList = appnameList;
    }

    @Override
    public String toString() {
        return "MonitorRequest{" +
                "env='" + env + '\'' +
                ", appnameList=" + appnameList +
                '}';
    }

}
