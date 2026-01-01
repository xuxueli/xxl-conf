package com.xxl.conf.core.openapi.confdata;

import com.xxl.conf.core.openapi.confdata.model.*;
import com.xxl.tool.response.Response;

/**
 * conf data service
 *
 * @author xuxueli 2025001-12
 */
public interface ConfDataService {

    /**
     *  1、query conf-key: 查询配置Key
     *
     *  <pre>
     *      Request：
     *      {
     *          "env":"test",
     *          "appnameList":[                 // List<{Appname}>
     *              "app01",
     *              "app02"
     *          ]
     *      }
     *      Response：
     *      {
     *          "code": 200,
     *          "data":{
     *              "appnameKeyList":{          // Map: Appname -> List<{Key}>
     *                  "app01":[
     *                      "key01",
     *                      "key02"
     *                  ],
     *                  "app02":[...]
     *              }
     *          }
     *      }
     *  </pre>
     *
     * @param request request
     * @return query key response
     */
    public Response<QueryKeyResponse> queryKey(QueryKeyRequest request);

    /**
     * 2、query conf-data: 查询配置数据/Md5
     *
     * logic：
     *      1、only read cache
     *
     *  2、queryData     ：
     *  <pre>
     *      请求：
     *      {
     *          "env":"test",
     *          "appnameKeyList":{                  // Map: Appname -> List<{Key}>
     *              "app01":[
     *                  "key01",
     *                  "key02"
     *              ],
     *              "app02":[...]
     *          },
     *          simpleQuery: true                   // Bool: true 仅查询md5；false 查询数据+md5;
     *      }
     *      响应：
     *      {
     *          "code": 200,
     *          "data":{
     *              "app01":{                       // Map: Appname -> Map<{Key}, ConfDataCacheDTO>
     *                  "key01": {                  // Map: {Key} -> ConfDataCacheDTO
     *                      "key": "key01",             // ConfDataCacheDTO: key
     *                      "value":"value01",          // ConfDataCacheDTO: value
     *                      "valueMd5":"md5"            // ConfDataCacheDTO: valueMd5
     *                  },
     *                  "key02": {...}
     *              },
     *              "app02":{...}
     *          }
     *      }
     *  </pre>
     *
     * @param request request
     * @return conf data
     */
    public Response<QueryDataResponse> queryData(QueryDataRequest request);

    /**
     * 3、monitor appname of conf-data
     *
     * logic：
     *      1、support client monitor，long-polling
     *      2、push client when data changed
     *
     *  <pre>
     *      Request：
     *      {
     *          "env":"test",
     *          "appnameList":[
     *              "app01",
     *              "app02"
     *          ]
     *      }
     *      Response：
     *      {
     *          "code": 200
     *      }
     *  </pre>
     *
     * @param request request
     * @return response
     */
    public Response<String> monitor(MonitorRequest request);

}
