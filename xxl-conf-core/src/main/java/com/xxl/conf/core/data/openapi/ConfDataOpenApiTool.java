package com.xxl.conf.core.data.openapi;

import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.http.HttpTool;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * conf data openapi tool
 *
 * @author xuxueli 2018-11-29
 */
public class ConfDataOpenApiTool {

    public static class QueryConfDataRequest implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * accessToken
         */
        private String accessToken;

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

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

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
    }

    public static class QueryConfDataResponse implements Serializable {
        public static final long serialVersionUID = 42L;

        public static final int SUCCESS_CODE = 200;
        public static final int FAIL_CODE = 203;

        private int code;

        private String msg;

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

        public QueryConfDataResponse() {
        }
        public QueryConfDataResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

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
            return "QueryConfDataResponse{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", confData=" + confData +
                    ", confDataMd5=" + confDataMd5 +
                    '}';
        }

        // success
        public boolean isSuccess() {
            return code == SUCCESS_CODE;
        }
    }

    /**
     * discovery
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param confKey
     * @param simpleQuery
     * @return
     */
    public static QueryConfDataResponse query(String adminAddress,
                                             String accessToken,
                                             String env,
                                             Map<String, List<String>> confKey,
                                             boolean simpleQuery) {

        QueryConfDataRequest request = new QueryConfDataRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setConfKey(confKey);
        request.setSimpleQuery(simpleQuery);

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/confdata/query",
                GsonTool.toJson(request),
                null,
                3000

        );
        QueryConfDataResponse response = GsonTool.fromJson(responseBody, QueryConfDataResponse.class);
        return response;
    }

    /**
     * monitor
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param confKey
     * @param simpleQuery
     * @param timeout   by second
     *
     * @return
     */
    public static QueryConfDataResponse monitor(String adminAddress,
                                               String accessToken,
                                               String env,
                                               Map<String, List<String>> confKey,
                                               boolean simpleQuery,
                                               int timeout) {

        QueryConfDataRequest request = new QueryConfDataRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setConfKey(confKey);
        request.setSimpleQuery(simpleQuery);

        try {
            String responseBody = HttpTool.postBody(adminAddress + "/openapi/confdata/monitor",
                    GsonTool.toJson(request),
                    null,
                    timeout
                    );
            QueryConfDataResponse response = GsonTool.fromJson(responseBody, QueryConfDataResponse.class);
            return response;
        } catch (Exception e) {
            return new QueryConfDataResponse(QueryConfDataResponse.FAIL_CODE, e.getMessage());
        }
    }


}
