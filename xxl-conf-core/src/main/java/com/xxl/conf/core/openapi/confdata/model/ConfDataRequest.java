package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ConfDataRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * <pre>
     *     {
     *         "env":"test",
     *         "confKey":{
     *             "app01": ["k1", "k2"],
     *             "app02": ["k3", "k4"]
     *         }
     *     }
     * </pre>
     */
    private Map<String, List<String>> confKey;

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

    public Map<String, List<String>> getConfKey() {
        return confKey;
    }

    public void setConfKey(Map<String, List<String>> confKey) {
        this.confKey = confKey;
    }

    public boolean isSimpleQuery() {
        return simpleQuery;
    }

    public void setSimpleQuery(boolean simpleQuery) {
        this.simpleQuery = simpleQuery;
    }

    @Override
    public String toString() {
        return "ConfDataRequest{" +
                "env='" + env + '\'' +
                ", confKey=" + confKey +
                ", simpleQuery=" + simpleQuery +
                '}';
    }
}
