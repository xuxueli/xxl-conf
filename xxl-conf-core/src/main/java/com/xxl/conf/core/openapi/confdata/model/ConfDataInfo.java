package com.xxl.conf.core.openapi.confdata.model;

import java.io.Serializable;
import java.util.Map;

public class ConfDataInfo implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * <pre>
     *     {
     *         "code": 200,
     *         "confData":{
     *              "app01":{
     *                  "k1": "v1",
     *                  "k2": "v2"
     *              }
     *         }
     *         "confDataMd5":{
     *              "app01":{
     *                  "k1": md5(data),
     *                  "k2": md5(data)
     *              }
     *         }
     *     }
     * </pre>
     */
    private Map<String, Map<String, String>> confData;

    private Map<String, Map<String, String>> confDataMd5;

    public Map<String, Map<String, String>> getConfData() {
        return confData;
    }

    public void setConfData(Map<String, Map<String, String>> confData) {
        this.confData = confData;
    }

    public Map<String, Map<String, String>> getConfDataMd5() {
        return confDataMd5;
    }

    public void setConfDataMd5(Map<String, Map<String, String>> confDataMd5) {
        this.confDataMd5 = confDataMd5;
    }

    @Override
    public String toString() {
        return "ConfDataInfo{" +
                "confData=" + confData +
                ", confDataMd5=" + confDataMd5 +
                '}';
    }

}
