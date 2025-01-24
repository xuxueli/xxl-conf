package com.xxl.conf.admin.openapi.confdata;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.annotation.Permission;
import com.xxl.conf.admin.openapi.confdata.biz.ConfDataBizService;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataRequest;
import com.xxl.conf.admin.openapi.common.model.OpenApiResponse;
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
@RequestMapping("/openapi/confdata")
public class ConfDataOpenApiController {
    private static Logger logger = LoggerFactory.getLogger(ConfDataOpenApiController.class);


    @Resource
    private ConfDataBizService confdataService;

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
            if ("query".equals(uri)) {
                /**
                 * 配置数据查询 API
                 * 说明：查询配置数据信息
                 */
                QueryConfDataRequest request = JSON.parseObject(data, QueryConfDataRequest.class);
                return confdataService.query(request);
            } else if ("monitor".equals(uri)) {
                /**
                 * 配置数据监控 API
                 * 说明：long-polling 接口，主动阻塞一段时间（默认30s）；直至阻塞超时或配置信息变动时响应；
                 */
                QueryConfDataRequest request = JSON.parseObject(data, QueryConfDataRequest.class);
                return confdataService.monitor(request);
            } else {
                return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Throwable e) {
            logger.error("openapi invoke error[{}].", e.getMessage(), e);
            return new OpenApiResponse(OpenApiResponse.FAIL_CODE, "openapi invoke error: " + e.getMessage());
        }

    }

}
