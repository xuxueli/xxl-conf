package com.xxl.conf.core.test.openapi.registry;

import com.xxl.conf.core.constant.Consts;
import com.xxl.conf.core.openapi.registry.RegistryService;
import com.xxl.conf.core.openapi.registry.model.DiscoveryData;
import com.xxl.conf.core.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.core.openapi.registry.model.RegisterInstance;
import com.xxl.conf.core.openapi.registry.model.RegisterRequest;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RegistryOpenApiTest {
    private static final Logger logger = LoggerFactory.getLogger(RegistryOpenApiTest.class);

    // admin-client
    private static final String adminAddress = "http://127.0.0.1:8080/xxl-conf-admin";
    private static final String accessToken = "defaultaccesstoken";

    private RegistryService getClient() {
        String finalUrl = adminAddress + "/openapi/registry";
        return HttpTool.createClient()
                .url(finalUrl)
                .timeout(3 * 1000)
                .header(Consts.XXL_CONF_ACCESS_TOKEN, accessToken)
                .proxy(RegistryService.class);
    }

    private RegistryService getMonitorClient() {
        String finalUrl = adminAddress + "/openapi/registry";
        return HttpTool.createClient()
                .url(finalUrl)
                .timeout(30 * 1000)
                .header(Consts.XXL_CONF_ACCESS_TOKEN, accessToken)
                .proxy(RegistryService.class);
    }


    @Test
    public void test_register() {
        // param
        RegisterRequest request = new RegisterRequest();
        request.setEnv("test");
        request.setInstance(new RegisterInstance("xxl-conf-sample", "127.0.0.1", 8080, "{}"));
        logger.info("request:{}", request);

        // invoke
        Response<String> response = getClient().register(request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

    @Test
    public void test_unregister() {
        // param
        RegisterRequest request = new RegisterRequest();
        request.setEnv("test");
        request.setInstance(new RegisterInstance("xxl-conf-sample", "127.0.0.1", 8080, "{}"));
        logger.info("request:{}", request);

        // invoke
        Response<String> response = getClient().unregister(request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

    @Test
    public void test_discovery() {
        // param
        DiscoveryRequest request = new DiscoveryRequest();
        request.setEnv("test");
        request.setAppnameList(List.of("xxl-conf-sample", "app02"));
        request.setSimpleQuery(false);

        // invoke
        Response<DiscoveryData> response = getClient().discovery(request);

        logger.info("request:{}", request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

    @Test
    public void test_monitor() {

        // param
        DiscoveryRequest request = new DiscoveryRequest();
        request.setEnv("test");
        request.setAppnameList(List.of("xxl-conf-sample", "app02"));
        request.setSimpleQuery(true);

        // invoke
        Response<String> response = getMonitorClient().monitor(request);

        logger.info("request:{}", request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

}
