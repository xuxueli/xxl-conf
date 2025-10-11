package com.xxl.conf.core.openapi.confdata;

import com.xxl.conf.core.openapi.confdata.model.ConfDataRequest;
import com.xxl.conf.core.openapi.confdata.model.ConfDataInfo;
import com.xxl.tool.response.Response;

/**
 * conf data service
 *
 * @author xuxueli 2025001-12
 */
public interface ConfDataService {

    /**
     * query conf data
     *
     * logic：
     *      1、only read cache
     *
     * @param request
     * @return
     */
    public Response<ConfDataInfo> query(ConfDataRequest request);

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
    public Response<String> monitor(ConfDataRequest request);

}
