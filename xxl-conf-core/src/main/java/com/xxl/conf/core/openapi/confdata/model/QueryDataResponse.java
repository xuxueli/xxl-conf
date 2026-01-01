package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * QueryData Response
 *
 * @author xuxueli 2025-01-12
 */
public class QueryDataResponse implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * AppName -> Map<{Key}, ConfDataCacheDTO>
     */
    private Map<String, Map<String, ConfDataCacheDTO>> confData;

    public Map<String, Map<String, ConfDataCacheDTO>> getConfData() {
        return confData;
    }

    public void setConfData(Map<String, Map<String, ConfDataCacheDTO>> confData) {
        this.confData = confData;
    }

    @Override
    public String toString() {
        return "QueryDataResponse{" +
                "confData=" + confData +
                '}';
    }

}
