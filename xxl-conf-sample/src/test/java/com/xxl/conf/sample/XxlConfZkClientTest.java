package com.xxl.conf.sample;


import com.xxl.conf.core.core.XxlConfZkConf;
import org.apache.zookeeper.KeeperException;

public class XxlConfZkClientTest {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        XxlConfZkConf.set("key02", "666");
        System.out.println(XxlConfZkConf.get("key02"));
        XxlConfZkConf.delete("key02");
    }

}
