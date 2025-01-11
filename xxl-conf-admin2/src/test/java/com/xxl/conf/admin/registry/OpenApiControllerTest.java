package com.xxl.conf.admin.registry;

import com.xxl.conf.admin.registry.openapi.RegisterTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class OpenApiControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiControllerTest.class);

    // admin-client
    private static String adminAddress = "http://127.0.0.1:8080/xxl-conf-admin";
    private static String accessToken = "defaultaccesstoken";
    private static String env = "test";


    @Test
    public void test_register() {

        // 1、register
        RegisterTool.RegisterInstance instance = new RegisterTool.RegisterInstance("xxl-conf-sample-springboot-server", "127.0.0.1", 8080, "{}");
        RegisterTool.OpenApiResponse openApiResponse = RegisterTool.register(adminAddress, accessToken, env, instance);

        logger.info("result:{}, instance:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", instance, openApiResponse);
    }

    @Test
    public void test_unregister() {

        // 2、unregister
        RegisterTool.RegisterInstance instance = new RegisterTool.RegisterInstance("xxl-conf-sample-springboot-server", "127.0.0.1", 8080, "{}");
        RegisterTool.OpenApiResponse openApiResponse = RegisterTool.unregister(adminAddress, accessToken, env, instance);

        logger.info("result:{}, instance:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", instance, openApiResponse);
    }

    @Test
    public void test_discovery() {

        // 3、discovery
        List<String> appnameList = Arrays.asList("app01", "app02");
        RegisterTool.DiscoveryResponse openApiResponse = RegisterTool.discovery(adminAddress, accessToken, env, appnameList, false);

        logger.info("result:{}, appnameList:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", appnameList, openApiResponse);
    }

    @Test
    public void test_monitor() {

        // 4、monitor
        List<String> appnameList = Arrays.asList("app01", "app02");
        RegisterTool.OpenApiResponse openApiResponse = RegisterTool.monitor(adminAddress, accessToken, env, appnameList, 30);

        logger.info("result:{}, appnameList:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", appnameList, openApiResponse);
    }

}
