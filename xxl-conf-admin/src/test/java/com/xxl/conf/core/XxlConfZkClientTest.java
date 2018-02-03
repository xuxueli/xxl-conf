package com.xxl.conf.core;

import com.xxl.conf.core.core.XxlConfZkClient;
import com.xxl.conf.core.env.Environment;
import org.apache.zookeeper.KeeperException;

public class XxlConfZkClientTest {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        XxlConfZkClient.setPathDataByKey("key02", "666");
        System.out.println(XxlConfZkClient.getPathDataByKey("key02"));
        XxlConfZkClient.deletePathByKey("key02");

    }

}
