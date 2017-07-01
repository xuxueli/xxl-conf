package com.xxl.conf.core.util;

import com.xxl.conf.core.XxlConfClient;

/**
 * 环境基类
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {

	/**
	 * conf data path in zk
     */
	public static final String CONF_DATA_PATH = "/xxl-conf";

	/**
	 * zk config file
     *//*
	private static final String ZK_ADDRESS_FILE = "/data/webapps/xxl-conf.properties";*/

	/**
	 * zk address
     */
	public static final String ZK_ADDRESS;		// zk地址：格式	ip1:port,ip2:port,ip3:port
	static {
		/*Properties prop = PropertiesUtil.loadFileProperties(ZK_ADDRESS_FILE);*/
		ZK_ADDRESS = PropertiesUtil.getString(XxlConfClient.localProp, "xxl.conf.zkserver");
	}

	public static void main(String[] args) {
		System.out.println(ZK_ADDRESS);
	}
}

