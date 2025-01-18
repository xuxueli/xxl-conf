package com.xxl.conf.core.data;

import com.xxl.conf.core.data.tool.ConfDataTool;
import com.xxl.conf.core.factory.XxlConfFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xuxueli 2018-11-28
 */
public class XxlConfRemoteHelper {
    private static Logger logger = LoggerFactory.getLogger(XxlConfRemoteHelper.class);

    private XxlConfFactory xxlConfFactory;
    public XxlConfRemoteHelper(final XxlConfFactory xxlConfFactory) {
        this.xxlConfFactory = xxlConfFactory;
    }


    /**
     * query
     *
     * @param confKey
     * @param simpleQuery
     * @return
     */
    public ConfDataTool.QueryConfDataResponse query(Map<String, List<String>> confKey, boolean simpleQuery){
        String addressTmp = xxlConfFactory.getAddressList().size()>1
                ? xxlConfFactory.getAddressList().get(ThreadLocalRandom.current().nextInt(xxlConfFactory.getAddressList().size()))
                : xxlConfFactory.getAddressList().get(0);
        return ConfDataTool.query(addressTmp, xxlConfFactory.getAccesstoken(), xxlConfFactory.getEnv(), confKey, simpleQuery);
    }

    /**
     * monitor
     *
     * @param confKey
     * @param simpleQuery
     * @return
     */
    public ConfDataTool.QueryConfDataResponse monitor(Map<String, List<String>> confKey, boolean simpleQuery){
        String addressTmp = xxlConfFactory.getAddressList().size()>1
                ? xxlConfFactory.getAddressList().get(ThreadLocalRandom.current().nextInt(xxlConfFactory.getAddressList().size()))
                : xxlConfFactory.getAddressList().get(0);
        return ConfDataTool.monitor(addressTmp, xxlConfFactory.getAccesstoken(), xxlConfFactory.getEnv(), confKey, simpleQuery, 30);
    }

}
