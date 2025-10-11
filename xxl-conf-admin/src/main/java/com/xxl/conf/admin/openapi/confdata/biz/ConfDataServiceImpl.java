package com.xxl.conf.admin.openapi.confdata.biz;

import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.core.openapi.confdata.ConfDataService;
import com.xxl.conf.core.openapi.confdata.model.ConfDataRequest;
import com.xxl.conf.core.openapi.confdata.model.ConfDataInfo;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

@Service
public class ConfDataServiceImpl implements ConfDataService {

    @Override
    public Response<ConfDataInfo> query(ConfDataRequest request) {
        return ConfDataBootstrap.getInstance().getConfDataCacheHelpler().queryConfData(request);
    }

    @Override
    public Response<String> monitor(ConfDataRequest request) {
        return null;
    }

}
