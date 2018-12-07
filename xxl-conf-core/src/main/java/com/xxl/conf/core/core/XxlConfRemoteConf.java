package com.xxl.conf.core.core;

import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.model.XxlConfParamVO;
import com.xxl.conf.core.util.BasicHttpUtil;
import com.xxl.conf.core.util.json.BasicJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author xuxueli 2018-11-28
 */
public class XxlConfRemoteConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfRemoteConf.class);


    private static String adminAddress;
    private static String env;
    private static String accessToken;

    private static List<String> adminAddressArr = null;

    public static void init(String adminAddress, String env, String accessToken) {

        // valid
        if (adminAddress==null || adminAddress.trim().length()==0) {
            throw new XxlConfException("xxl-conf adminAddress can not be empty");
        }
        if (env==null || env.trim().length()==0) {
            throw new XxlConfException("xxl-conf env can not be empty");
        }


        XxlConfRemoteConf.adminAddress = adminAddress;
        XxlConfRemoteConf.env = env;
        XxlConfRemoteConf.accessToken = accessToken;


        // parse
        XxlConfRemoteConf.adminAddressArr = new ArrayList<>();
        if (adminAddress.contains(",")) {
            XxlConfRemoteConf.adminAddressArr.add(adminAddress);
        } else {
            XxlConfRemoteConf.adminAddressArr.addAll(Arrays.asList(adminAddress.split(",")));
        }

    }


    // ---------------------- rest api ----------------------

    /**
     * get and valid
     *
     * @param url
     * @param requestBody
     * @param timeout
     * @return
     */
    private static Map<String, Object> getAndValid(String url, String requestBody, int timeout){

        // resp json
        String respJson = BasicHttpUtil.postBody(url, requestBody, timeout);
        if (respJson == null) {
            return null;
        }

        // parse obj
        Map<String, Object> respObj = BasicJson.parseMap(respJson);
        int code = Integer.valueOf(String.valueOf(respObj.get("code")));
        if (code != 200) {
            logger.info("request fail, msg={}", (respObj.containsKey("msg")?respObj.get("msg"):respJson) );
            return null;
        }
        return respObj;
    }


    /**
     * find
     *
     * @param keys
     * @return
     */
    public static Map<String, String> find(Set<String> keys) {
        for (String adminAddressUrl: XxlConfRemoteConf.adminAddressArr) {

            // url + param
            String url = adminAddressUrl + "/conf/find";

            XxlConfParamVO paramVO = new XxlConfParamVO();
            paramVO.setAccessToken(accessToken);
            paramVO.setEnv(env);
            paramVO.setKeys(new ArrayList<String>(keys));

            String paramsJson = BasicJson.toJson(paramVO);

            // get and valid
            Map<String, Object> respObj = getAndValid(url, paramsJson, 5);

            // parse
            if (respObj!=null && respObj.containsKey("data")) {
                Map<String, String> data = (Map<String, String>) respObj.get("data");
                return data;
            }
        }

        return null;
    }

    public static String find(String key) {
        Map<String, String> result = find(new HashSet<String>(Arrays.asList(key)));
        if (result!=null) {
            return result.get(key);
        }
        return null;
    }


    /**
     * monitor
     *
     * @param keys
     * @return
     */
    public static boolean monitor(Set<String> keys) {

        for (String adminAddressUrl: XxlConfRemoteConf.adminAddressArr) {

            // url + param
            String url = adminAddressUrl + "/conf/monitor";

            XxlConfParamVO paramVO = new XxlConfParamVO();
            paramVO.setAccessToken(accessToken);
            paramVO.setEnv(env);
            paramVO.setKeys(new ArrayList<String>(keys));

            String paramsJson = BasicJson.toJson(paramVO);

            // get and valid
            Map<String, Object> respObj = getAndValid(url, paramsJson, 60);

            return respObj!=null?true:false;
        }
        return false;
    }

}
