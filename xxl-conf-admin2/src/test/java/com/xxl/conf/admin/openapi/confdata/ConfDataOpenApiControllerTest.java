package com.xxl.conf.admin.openapi.confdata;

import com.xxl.conf.admin.openapi.confdata.tool.ConfDataTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfDataOpenApiControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfDataOpenApiControllerTest.class);

    // admin-client
    private static String adminAddress = "http://127.0.0.1:8081/xxl-conf-admin";
    private static String accessToken = "defaultaccesstoken";
    private static String env = "test";

    @Test
    public void query() {

        // 1、discovery
        Map<String, List<String>> confKey = new HashMap<>();
        confKey.put("app01", Arrays.asList("key1", "key2"));

        ConfDataTool.QueryConfDataResponse openApiResponse = ConfDataTool.query(adminAddress, accessToken, env, confKey, false);

        logger.info("result:{}, confKey:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", confKey, openApiResponse);
    }

    @Test
    public void monitor() {

        // 2、monitor
        Map<String, List<String>> confKey = new HashMap<>();
        confKey.put("app01", Arrays.asList("key1", "key2"));

        ConfDataTool.QueryConfDataResponse openApiResponse = ConfDataTool.monitor(adminAddress, accessToken, env, confKey, false, 30);

        logger.info("result:{}, confKey:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", confKey, openApiResponse);
    }

}
