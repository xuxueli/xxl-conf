package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * QueryKey Request
 *
 * @author xuxueli 2025-01-12
 */
public class QueryDataRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * AppName -> List<Key>
     */
    private Map<String, List<String>> appnameKeyData;

    /**
     * simple Query
     *      true: only summary data (md5)
     *      false: query all data (detail + md5)
     */
    private boolean simpleQuery;

    public QueryDataRequest() {
    }
    public QueryDataRequest(String env, Map<String, List<String>> appnameKeyData, boolean simpleQuery) {
        this.env = env;
        this.appnameKeyData = appnameKeyData;
        this.simpleQuery = simpleQuery;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Map<String, List<String>> getAppnameKeyData() {
        return appnameKeyData;
    }

    public void setAppnameKeyData(Map<String, List<String>> appnameKeyData) {
        this.appnameKeyData = appnameKeyData;
    }

    public boolean isSimpleQuery() {
        return simpleQuery;
    }

    public void setSimpleQuery(boolean simpleQuery) {
        this.simpleQuery = simpleQuery;
    }

    @Override
    public String toString() {
        return "QueryDataRequest{" +
                "env='" + env + '\'' +
                ", appnameKeyData=" + appnameKeyData +
                ", simpleQuery=" + simpleQuery +
                '}';
    }

}
