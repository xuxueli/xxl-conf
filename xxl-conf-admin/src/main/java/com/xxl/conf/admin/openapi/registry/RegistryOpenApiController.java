package com.xxl.conf.admin.openapi.registry;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.openapi.registry.biz.RegistryService;
import com.xxl.conf.admin.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.admin.openapi.common.model.OpenApiResponse;
import com.xxl.conf.admin.openapi.registry.model.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xuxueli 2018-11-29
 */
@Controller
@RequestMapping("/openapi/registry")
public class RegistryOpenApiController {
    private static Logger logger = LoggerFactory.getLogger(RegistryOpenApiController.class);


    @Resource
    private RegistryService registryService;

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
            if ("register".equals(uri)) {
                /**
                 * 服务注册 & 续约 API
                 * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
                 */
                RegisterRequest request = JSON.parseObject(data, RegisterRequest.class);
                return registryService.register(request);
            } else if ("unregister".equals(uri)) {
                /**
                 * 服务摘除 API
                 * 说明：新服务摘除下线1s内广播通知接入方；
                 */
                RegisterRequest request = JSON.parseObject(data, RegisterRequest.class);
                return registryService.unregister(request);
            } else if ("discovery".equals(uri)) {
                /**
                 * 服务发现 API
                 * 说明：查询在线服务地址列表；
                 */
                DiscoveryRequest request = JSON.parseObject(data, DiscoveryRequest.class);
                return registryService.discovery(request);
            } else if ("monitor".equals(uri)) {
                /**
                 * 服务监控 API
                 * 说明：long-polling 接口，主动阻塞一段时间（默认30s）；直至阻塞超时或服务注册信息变动时响应；
                 */
                DiscoveryRequest request = JSON.parseObject(data, DiscoveryRequest.class);
                return registryService.monitor(request);
            } else {
                return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Throwable e) {
            logger.error("openapi invoke error[{}].", e.getMessage(), e);
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "openapi invoke error: " + e.getMessage());
        }

    }

}
