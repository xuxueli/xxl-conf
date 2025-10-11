package com.xxl.conf.core.openapi.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryRequest implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * instance list which want discovery
     */
    private List<String> appnameList;

    /**
     * simple Query
     *      true: only summary data (md5)
     *      false: query all data (detail + md5)
     */
    private boolean simpleQuery;

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

    public boolean isSimpleQuery() {
        return simpleQuery;
    }

    public void setSimpleQuery(boolean simpleQuery) {
        this.simpleQuery = simpleQuery;
    }

}
