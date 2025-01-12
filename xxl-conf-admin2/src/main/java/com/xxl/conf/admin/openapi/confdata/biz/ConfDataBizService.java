package com.xxl.conf.admin.openapi.confdata.biz;

import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataRequest;
import com.xxl.conf.admin.openapi.confdata.model.QueryConfDataResponse;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * conf data service
 *
 * @author xuxueli 2025001-12
 */
public interface ConfDataBizService {

    /**
     * query conf data
     *
     * logic：
     *      1、only read cache
     *
     * @param request
     * @return
     */
    public QueryConfDataResponse query(QueryConfDataRequest request);

    /**
     * monitor conf data
     *
     * logic：
     *      1、support client monitor，long-polling
     *      2、push client when data changed
     *
     * @param request
     * @return
     */
    public DeferredResult<QueryConfDataResponse> monitor(QueryConfDataRequest request);

}
