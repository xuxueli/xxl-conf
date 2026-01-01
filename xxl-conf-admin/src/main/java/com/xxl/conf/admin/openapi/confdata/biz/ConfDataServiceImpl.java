package com.xxl.conf.admin.openapi.confdata.biz;

import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

@Service
public class ConfDataServiceImpl implements ConfDataService {
    @Override
    public Response<QueryKeyResponse> queryKey(QueryKeyRequest request) {
        return ConfDataBootstrap.getInstance().getConfDataCacheHelpler().queryKey(request);
    }

    @Override
    public Response<QueryDataResponse> queryData(QueryDataRequest request) {
        return ConfDataBootstrap.getInstance().getConfDataCacheHelpler().queryData(request);
    }

    @Override
    public Response<String> monitor(MonitorRequest request) {
        // OpenApiController: ConfDataBootstrap.getInstance().getConfDataDeferredResultHelpler().monitor(request);
        return null;
    }

}
