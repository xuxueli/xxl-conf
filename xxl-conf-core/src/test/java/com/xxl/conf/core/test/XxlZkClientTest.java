package com.xxl.conf.core.test;

import com.xxl.conf.core.util.XxlZkClient;

public class XxlZkClientTest {

    public static void main(String[] args) throws InterruptedException {
        XxlZkClient client = new XxlZkClient("127.0.0.1:2181", "/xxl-conf", null, null);

        System.out.println(client.getClient());
        System.out.println(client.getClient());
    }

}
