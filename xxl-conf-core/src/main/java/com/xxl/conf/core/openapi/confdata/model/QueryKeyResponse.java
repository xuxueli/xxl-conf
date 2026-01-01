package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * QueryKey Response
 *
 * @author xuxueli 2025-01-12
 */
public class QueryKeyResponse implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * AppName -> List<Key>
     */
    private Map<String, List<String>> appnameKeyData;

    public Map<String, List<String>> getAppnameKeyData() {
        return appnameKeyData;
    }

    public void setAppnameKeyData(Map<String, List<String>> appnameKeyData) {
        this.appnameKeyData = appnameKeyData;
    }

    @Override
    public String toString() {
        return "QueryKeyResponse{" +
                "appnameKeyData=" + appnameKeyData +
                '}';
    }

}
