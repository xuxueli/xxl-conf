package com.xxl.cfg.util;

import java.util.Properties;

/**
 * 环境基类
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {
	private static String environment_file = "/data/webapps/xxl-conf.properties";
	private static String base_dir = "/xxl-conf";
	
	// 环境：beta qa	product
	private static String deployenv;
	// zk地址：格式	ip1:port,ip2:port,ip3:port
	private static String zkserver;
	
	static {
		Properties prop = PropertiesUtil.loadProperties(environment_file, false);
		deployenv = PropertiesUtil.getString(prop, "deployenv", null);
		zkserver = PropertiesUtil.getString(prop, "zkserver", null);
		
		if (!("beta".equals(deployenv) || "qa".equals(deployenv) || "product".equals(deployenv))) {
			deployenv = "beta";
		}
	}
	
	public static String getZkserver() {
		return zkserver;
	}
	public static String getDeployenvPath() {
		return base_dir.concat("/").concat(deployenv);
	}
	
	public static void main(String[] args) {
		System.out.println(getDeployenvPath());
		System.out.println(getZkserver());
	}
}

