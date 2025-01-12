package com.xxl.conf.admin.openapi.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryRequest extends OpenApiRequest implements Serializable {
    public static final long serialVersionUID = 42L;

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
