package com.xxl.conf.admin.openapi;

import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.conf.core.constant.Consts;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.conf.core.openapi.confdata.model.MonitorRequest;
import com.xxl.conf.core.openapi.confdata.model.QueryDataRequest;
import com.xxl.conf.core.openapi.confdata.model.QueryKeyRequest;
import com.xxl.conf.core.openapi.registry.RegistryService;
import com.xxl.conf.core.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.core.openapi.registry.model.RegisterRequest;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author xuxueli 2018-11-29
 */
@Controller
public class OpenApiController {
    private static Logger logger = LoggerFactory.getLogger(OpenApiController.class);

    @Resource
    private ConfDataService confdataService;
    @Resource
    private RegistryService registryService;

    @RequestMapping("/openapi/{service}/{uri}")
    @ResponseBody
    @XxlSso(login = false)
    public Object api(HttpServletRequest request,
                      @PathVariable("service") String service,
                      @PathVariable("uri") String uri,
                      @RequestHeader(Consts.XXL_CONF_ACCESS_TOKEN) String accesstoken,
                      @RequestBody(required = false) String requestBody){

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return Response.ofFail("invalid request, HttpMethod not support.");
        }
        if (StringTool.isBlank(service)) {
            return Response.ofFail("invalid request, service empty.");
        }
        if (StringTool.isBlank(uri)) {
            return Response.ofFail("invalid request, uri-mapping empty.");
        }
        // valid token
        if (!RegistryBootstrap.getInstance().getAccessTokenHelpler().validRequestToken(accesstoken)) {
            return Response.ofFail("accessToken Invalid.");
        }

        // dispatch service
        switch (service){
            case "confdata":
                return confdata(uri, requestBody);
            case "registry":
                return registry(uri, requestBody);
            default:
                return Response.ofFail("invalid request, service("+ service +") not found.");
        }
    }

    /**
     * confdata
     */
    private Object confdata(String uri, String data){
        // dispatch request
        try {
            switch (uri) {
                case "queryKey": {
                    /**
                     * 配置Key查询 API
                     * 说明：查询配置Key信息
                     */
                    QueryKeyRequest request = GsonTool.fromJson(data, QueryKeyRequest.class);
                    return confdataService.queryKey(request);
                }
                case "queryData": {
                    /**
                     * 配置数据查询 API
                     * 说明：查询配置数据信息
                     */
                    QueryDataRequest request = GsonTool.fromJson(data, QueryDataRequest.class);
                    return confdataService.queryData(request);
                }
                case "monitor":{
                    /**
                     * 配置数据监控 API
                     * 说明：long-polling 接口，主动阻塞一段时间（默认30s）；直至阻塞超时或配置信息变动时响应；
                     */
                    MonitorRequest request = GsonTool.fromJson(data, MonitorRequest.class);
                    return ConfDataBootstrap.getInstance().getConfDataDeferredResultHelpler().monitor(request);
                    // return confdataService.monitor(request);
                }
                default:
                    return Response.ofFail("invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Exception e) {
            return Response.ofFail("openapi invoke error: " + e.getMessage());
        }
    }

    /**
     * registry
     */
    private Object registry(String uri, String data){
        // dispatch request
        try {
            switch (uri) {
                case "register":{
                    /**
                     * 服务注册 & 续约 API
                     * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
                     */
                    RegisterRequest request = GsonTool.fromJson(data, RegisterRequest.class);
                    return registryService.register(request);
                }
                case "unregister": {
                    /**
                     * 服务摘除 API
                     * 说明：新服务摘除下线1s内广播通知接入方；
                     */
                    RegisterRequest request = GsonTool.fromJson(data, RegisterRequest.class);
                    return registryService.unregister(request);
                }
                case "discovery": {
                    /**
                     * 服务发现 API
                     * 说明：查询在线服务地址列表；
                     */
                    DiscoveryRequest request = GsonTool.fromJson(data, DiscoveryRequest.class);
                    return registryService.discovery(request);
                }
                case "monitor": {
                    /**
                     * 服务监控 API
                     * 说明：long-polling 接口，主动阻塞一段时间（默认30s）；直至阻塞超时或服务注册信息变动时响应；
                     */
                    DiscoveryRequest request = GsonTool.fromJson(data, DiscoveryRequest.class);
                    return RegistryBootstrap.getInstance().getRegistryDeferredResultHelpler().monitor(request);
                    // return registryService.monitor(request);
                }
                default:
                    return Response.ofFail("invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Exception e) {
            return Response.ofFail("openapi invoke error: " + e.getMessage());
        }
    }

}
