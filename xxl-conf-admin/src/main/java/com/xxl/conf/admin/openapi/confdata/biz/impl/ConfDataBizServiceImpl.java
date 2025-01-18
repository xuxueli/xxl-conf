package com.xxl.conf.admin.openapi.confdata.biz.impl;

import com.xxl.conf.admin.openapi.confdata.biz.ConfDataBizService;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataFactory;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataRequest;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class ConfDataBizServiceImpl implements ConfDataBizService {

    @Override
    public QueryConfDataResponse query(QueryConfDataRequest request) {
        return ConfDataFactory.getInstance().getConfDataCacheHelpler().queryConfData(request);
    }

    @Override
    public DeferredResult<QueryConfDataResponse> monitor(QueryConfDataRequest request) {
        return ConfDataFactory.getInstance().getConfDataDeferredResultHelpler().monitor(request);
    }

}
