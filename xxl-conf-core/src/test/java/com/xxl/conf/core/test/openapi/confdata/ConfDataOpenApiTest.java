package com.xxl.conf.core.test.openapi.confdata;

import com.xxl.conf.core.constant.Consts;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.conf.core.openapi.confdata.model.ConfDataInfo;
import com.xxl.conf.core.openapi.confdata.model.ConfDataRequest;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ConfDataOpenApiTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfDataOpenApiTest.class);

    // admin-client
    private static final String adminAddress = "http://127.0.0.1:8080/xxl-conf-admin";
    private static final String accessToken = "defaultaccesstoken";

    private ConfDataService getClient() {
        String finalUrl = adminAddress + "/openapi/confdata";
        return HttpTool.createClient()
                .url(finalUrl)
                .timeout(3 * 1000)
                .header(Consts.XXL_CONF_ACCESS_TOKEN, accessToken)
                .proxy(ConfDataService.class);
    }

    private ConfDataService getMonitorClient() {
        String finalUrl = adminAddress + "/openapi/confdata";
        return HttpTool.createClient()
                .url(finalUrl)
                .timeout(30 * 1000)
                .header(Consts.XXL_CONF_ACCESS_TOKEN, accessToken)
                .proxy(ConfDataService.class);
    }

    @Test
    public void query() {
        // param
        ConfDataRequest request = new ConfDataRequest();
        request.setEnv("test");
        request.setConfKey(MapTool.newMap(
                "xxl-conf-sample", Arrays.asList("sample.key01", "sample.key02", "sample.key22")
        ));
        request.setSimpleQuery(false);
        logger.info("request:{}", request);

        // invoke
        Response<ConfDataInfo> response = getClient().query(request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

    @Test
    public void monitor() {
        // param
        ConfDataRequest request = new ConfDataRequest();
        request.setEnv("test");
        request.setConfKey(MapTool.newMap(
                "xxl-conf-sample", Arrays.asList("sample.key01", "sample.key02", "sample.key22")
        ));
        request.setSimpleQuery(true);
        logger.info("request:{}", request);

        // invoke
        Response<String> response = getMonitorClient().monitor(request);
        logger.info("result:{}, response:{}", response.isSuccess()?"success":"fail", response);
    }

}
