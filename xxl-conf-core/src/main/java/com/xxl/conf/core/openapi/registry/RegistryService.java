package com.xxl.conf.core.openapi.registry;

import com.xxl.conf.core.openapi.registry.model.DiscoveryRequest;
import com.xxl.conf.core.openapi.registry.model.DiscoveryData;
import com.xxl.conf.core.openapi.registry.model.RegisterRequest;
import com.xxl.tool.response.Response;

/**
 * @author xuxueli 2018-12-03
 */
public interface RegistryService {

    /**
     * register
     *
     * logic：
     *      1、async run -> write db + broadcast message -> refresh cache + push client
     *      2、single-client register single-app
     *
     * @param request   client instance
     * @return
     */
    Response<String> register(RegisterRequest request);

    /**
     * unregister
     *
     * @param request
     * @return
     */
    Response<String> unregister(RegisterRequest request);

    /**
     * discovery
     *
     * logic：
     *      1、only read cache
     */
    Response<DiscoveryData> discovery(DiscoveryRequest request);

    /**
     * monitor
     *
     * logic：
     *      1、support client monitor，long-polling
     *      2、push client when registry changed
     */
    Response<String> monitor(DiscoveryRequest request);

}
