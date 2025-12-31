package com.xxl.conf.core.openapi.confdata;

import com.xxl.conf.core.openapi.confdata.model.ConfDataInfo;
import com.xxl.conf.core.openapi.confdata.model.ConfDataRequest;
import com.xxl.tool.response.Response;

/**
 * conf data service
 *
 * @author xuxueli 2025001-12
 */
public interface ConfDataService {


    /**
     *     - 启动预热：启动时查询全量key，分批查询全量配置数据预热；
     *     - 全量刷新：定期全量检测配置md5摘要，对比本地与远程数据，不一致主动刷新；60s/次；
     *     - 增量更新：监听appkey，监听配置变更，触发全量比对刷新；
     *     - 降级：全量比度内存数据与本地file（“/xxl-conf/env/clustor/confdata/appkey.prop”），不一致更新本地数据；5min/次；
     *
     *
     *  0、数据结构：
     *  <pre>
     *      // 缓存数据：
     *      {
     *          "test##app01":{                         // key   : "{Env}##{Appname}"
     *              "key01":"value01",                  // value : Map<{Key}, ConfDataCacheDTO>
 *                  "key02":"value02"
     *          }
     *      }
     *      // 缓存数据Md5：
     *      {
     *          "test##app01":{                         // key  ： "{Env}##{Appname}"
     *              "key01":"md5",                      // Key  :  Map<{Key}, {配置数据MD5}>
 *                  "key02":"md5"
     *          }
     *      }
     *  </pre>
     *
     *  1、queryKey  : 查询配置Key
     *  <pre>
     *      请求：
     *      {
     *          "env":"test",
     *          "appname":"app01"
     *      }
     *      响应：
     *      {
     *          "code": 200,
     *          "data":[
     *              "key01",
     *              "key02"
     *          ]
     *      }
     *  </pre>
     *
     *  2、query     ：查询配置数据/Md5
     *  <pre>
     *      请求：
     *      {
     *          "env":"test",
     *          "appname":"app01"
     *          "keyList": [
     *              "key01",
     *              "key02"
     *          ],
     *          simpleQuery: true       // true 查询配置；false 查询md5;
     *      }
     *      响应：
     *      {
     *          "code": 200,
     *          "data":{
     *              "confData":{
     *                  "k1": "v1",
     *                  "k2": "v2"
     *              }
     *              "confDataMd5":{
     *                  "k1": md5(data),
     *                  "k2": md5(data)
     *              }
     *          }
     *      }
     *  </pre>
     *
     *  3、monitor     ：配置监听
     *  <pre>
     *      请求：
     *      {
     *          "env":"test",
     *          "appname":"app01"
     *      }
     *      响应：
     *      {
     *          "code": 200
     *      }
     *  </pre>
     */


    /**
     * query conf data
     *
     * logic：
     *      1、only read cache
     *
     * @param request request
     * @return conf data
     */
    public Response<ConfDataInfo> query(ConfDataRequest request);

    /**
     * monitor conf data
     *
     * logic：
     *      1、support client monitor，long-polling
     *      2、push client when data changed
     *
     * @param request request
     * @return response
     */
    public Response<String> monitor(ConfDataRequest request);

}
