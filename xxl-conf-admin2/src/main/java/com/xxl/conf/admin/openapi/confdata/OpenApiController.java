package com.xxl.conf.admin.openapi.confdata;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.admin.openapi.registry.model.OpenApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xuxueli 2018-11-29
 */
@Controller
@RequestMapping("/openapi/confdata")
public class OpenApiController {
    private static Logger logger = LoggerFactory.getLogger(OpenApiController.class);



    @RequestMapping("/{uri}")
    @ResponseBody
    @Permission(login = false)
    public Object api(HttpServletRequest httpServletRequest, @PathVariable("uri") String uri, @RequestBody(required = false) String data){

        // valid
        if (!"POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri==null || uri.trim().isEmpty()) {
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "invalid request, uri-mapping empty.");
        }

        // services mapping
        try {
            if ("discovery".equals(uri)) {
                /**
                 * 配置数据查询 API
                 * 说明：查询配置数据信息
                 */
                DiscoveryRequest request = JSON.parseObject(data, DiscoveryRequest.class);
                return null;
            } else if ("monitor".equals(uri)) {
                /**
                 * 配置数据监控 API
                 * 说明：long-polling 接口，主动阻塞一段时间（三倍于注册中心心跳时间）；直至阻塞超时或信息变动时响应；
                 */
                DiscoveryRequest request = JSON.parseObject(data, DiscoveryRequest.class);
                return null;
            } else {
                return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Throwable e) {
            logger.error("openapi invoke error[{}].", e.getMessage(), e);
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "openapi invoke error: " + e.getMessage());
        }

    }

}
