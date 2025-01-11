package com.xxl.conf.admin.registry.openapi;

import com.alibaba.fastjson2.JSON;
import com.xxl.tool.net.HttpTool;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * register tool
 *
 * @author xuxueli 2025-01-01
 */
public class RegisterTool {

    // ---------------------- entity ----------------------

    public static class RegisterInstance implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * AppName（应用唯一标识）
         */
        private String appname;

        /**
         * 注册节点IP
         */
        private String ip;

        /**
         * 注册节点端口号
         */
        private int port;

        /**
         * 扩展信息（可选）
         */
        private String extendInfo;

        public RegisterInstance() {
        }
        public RegisterInstance(String appname, String ip, int port, String extendInfo) {
            this.appname = appname;
            this.ip = ip;
            this.port = port;
            this.extendInfo = extendInfo;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getExtendInfo() {
            return extendInfo;
        }

        public void setExtendInfo(String extendInfo) {
            this.extendInfo = extendInfo;
        }

    }

    public static class RegisterRequest implements Serializable {
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
         * client instance
         */
        private RegisterInstance instance;


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

        public RegisterInstance getInstance() {
            return instance;
        }

        public void setInstance(RegisterInstance instance) {
            this.instance = instance;
        }

    }

    public static class OpenApiResponse implements Serializable {
        public static final long serialVersionUID = 42L;

        public static final int SUCCESS_CODE = 200;
        public static final int FAIL_CODE = 203;


        private int code;

        private String msg;

        public OpenApiResponse() {}
        public OpenApiResponse(int code, String msg) {
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


        @Override
        public String toString() {
            return "OpenApiResponse{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }

        public boolean isSuccess() {
            return code == SUCCESS_CODE;
        }

    }

    public static class DiscoveryRequest implements Serializable {
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
         * instance list which want discovery
         */
        private List<String> appnameList;

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

    public static class DiscoveryResponse extends OpenApiResponse implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * discovery result data
         *
         * structure：Map
         * 		key：appname
         * 		value：List<RegisterInstance> = List ～ instance
         *
         */
        private Map<String, List<InstanceCacheDTO>> discoveryData;

        /**
         * discovery result data-md5
         *
         * structure：Map
         * 		key：appname
         * 		value：md5
         *
         */
        private Map<String, String> discoveryDataMd5;

        public DiscoveryResponse(){}
        public DiscoveryResponse(int code, String msg) {
            super(code, msg);
        }

        public Map<String, List<InstanceCacheDTO>> getDiscoveryData() {
            return discoveryData;
        }

        public void setDiscoveryData(Map<String, List<InstanceCacheDTO>> discoveryData) {
            this.discoveryData = discoveryData;
        }

        public Map<String, String> getDiscoveryDataMd5() {
            return discoveryDataMd5;
        }

        public void setDiscoveryDataMd5(Map<String, String> discoveryDataMd5) {
            this.discoveryDataMd5 = discoveryDataMd5;
        }

        @Override
        public String toString() {
            return "DiscoveryResponse{" +
                    ", discoveryData=" + discoveryData +
                    ", discoveryDataMd5=" + discoveryDataMd5 +
                    ", code=" + getCode() +
                    ", msg='" + getMsg() + '\'' +
                    '}';
        }
    }

    public static class InstanceCacheDTO implements Serializable {
        private static final long serialVersionUID = 42L;

        /**
         * Env（环境唯一标识）
         */
        private String env;

        /**
         * AppName（应用唯一标识）
         */
        private String appname;

        /**
         * 注册节点IP
         */
        private String ip;

        /**
         * 注册节点端口号
         */
        private int port;

        /**
         * 扩展信息
         */
        private String extendInfo;

        public InstanceCacheDTO() {
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getExtendInfo() {
            return extendInfo;
        }

        public void setExtendInfo(String extendInfo) {
            this.extendInfo = extendInfo;
        }

        @Override
        public String toString() {
            return "InstanceCacheDTO{" +
                    "env='" + env + '\'' +
                    ", appname='" + appname + '\'' +
                    ", ip='" + ip + '\'' +
                    ", port=" + port +
                    ", extendInfo='" + extendInfo + '\'' +
                    '}';
        }

        // tool

        /**
         * get sort key
         *
         * @return
         */
        public String getSortKey() {
            return ip + ":" + port;
        }

    }

    // ---------------------- tool ----------------------

    /**
     * register
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param instance
     * @return
     */
    public static OpenApiResponse register(String adminAddress,
                                                                   String accessToken,
                                                                   String env,
                                                                   RegisterInstance instance) {
        // 1、build request
        RegisterRequest request = new RegisterRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setInstance(instance);

        // 2、post
        String responseBody = HttpTool.postBody(adminAddress + "/openapi/register",
                JSON.toJSONString(request),
                null,
                3000);

        // 3、parse response
        OpenApiResponse openApiResponse = JSON.parseObject(responseBody, OpenApiResponse.class);
        return openApiResponse;
    }

    /**
     * unregister
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param instance
     * @return
     */
    public static OpenApiResponse unregister(String adminAddress,
                                                                    String accessToken,
                                                                    String env,
                                                                    RegisterInstance instance) {
        // 1、build request
        RegisterRequest request = new RegisterRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setInstance(instance);

        // 2、post
        String responseBody = HttpTool.postBody(adminAddress + "/openapi/unregister",
                JSON.toJSONString(request),
                null,
                3000);

        // 3、parse response
        OpenApiResponse openApiResponse = JSON.parseObject(responseBody, OpenApiResponse.class);
        return openApiResponse;
    }

    /**
     * discovery
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param appnameList
     * @param simpleQuery
     * @return
     */
    public static DiscoveryResponse discovery(String adminAddress,
                                                                    String accessToken,
                                                                    String env,
                                                                    List<String> appnameList,
                                                                    boolean simpleQuery) {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setAppnameList(appnameList);
        request.setSimpleQuery(simpleQuery);

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/discovery",
                JSON.toJSONString(request),
                null,
                3000
        );
        DiscoveryResponse discoveryResponse = JSON.parseObject(responseBody, DiscoveryResponse.class);
        return discoveryResponse;
    }

    /**
     * monitor
     *
     * @param adminAddress
     * @param accessToken
     * @param env
     * @param appnameList
     * @param timeout       by second
     * @return
     */
    public static OpenApiResponse monitor(String adminAddress,
                                          String accessToken,
                                          String env,
                                          List<String> appnameList,
                                          int timeout) {
        // 4、monitor
        DiscoveryRequest request = new DiscoveryRequest();
        request.setAccessToken(accessToken);
        request.setEnv(env);
        request.setAppnameList(appnameList);
        request.setSimpleQuery(false);

        try {
            String responseBody = HttpTool.postBody(adminAddress + "/openapi/monitor",
                    JSON.toJSONString(request),
                    null,
                    timeout);
            OpenApiResponse discoveryResponse = JSON.parseObject(responseBody, OpenApiResponse.class);
            return discoveryResponse;
        } catch (Exception e) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, e.getMessage());
        }
    }

}
