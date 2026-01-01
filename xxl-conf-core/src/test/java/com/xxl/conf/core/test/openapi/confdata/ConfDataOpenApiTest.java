package com.xxl.conf.core.test.openapi.confdata;

import com.xxl.conf.core.constant.Consts;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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
                .timeout(60 * 1000)
                .header(Consts.XXL_CONF_ACCESS_TOKEN, accessToken)
                .proxy(ConfDataService.class);
    }

    @Test
    public void queryKey() {
        // param
        QueryKeyRequest request = new QueryKeyRequest();
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("xxl-conf-sample", "xxl-conf-sample02"));
        logger.info("request:{}", request);

        // invoke
        Response<QueryKeyResponse> response = getClient().queryKey(request);
        logger.info("response:{}", GsonTool.toJsonPretty(response));
    }

    @Test
    public void queryData() {
        // param
        QueryDataRequest request = new QueryDataRequest();
        request.setEnv("test");
        request.setAppnameKeyData(MapTool.newMap(
                "xxl-conf-sample", List.of("sample.key01", "sample.key02", "sample.key03", "key04"),
                "xxl-conf-sample02", List.of("key01", "key02")
        ));
        request.setSimpleQuery(false);
        logger.info("request:{}", request);

        // invoke
        Response<QueryDataResponse> response = getClient().queryData(request);
        logger.info("response:{}", GsonTool.toJsonPretty(response));

        // invoke2
        request.setSimpleQuery(true);
        response = getClient().queryData(request);
        logger.info("response:{}", GsonTool.toJsonPretty(response));
    }

    @Test
    public void monitor() {
        // param
        MonitorRequest request = new MonitorRequest();
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("xxl-conf-sample", "xxl-conf-sample02"));
        logger.info("request:{}", request);

        // invoke
        Response<String> response = getMonitorClient().monitor(request);
        logger.info("response:{}", GsonTool.toJsonPretty(response));
    }

}
