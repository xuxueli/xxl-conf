package com.xxl.conf.admin.openapi.confdata.model;

import com.xxl.conf.admin.openapi.common.model.OpenApiRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class QueryConfDataRequest extends OpenApiRequest implements Serializable {
    private static final long serialVersionUID = 42L;

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

}
