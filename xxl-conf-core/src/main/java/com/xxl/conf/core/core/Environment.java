package com.xxl.conf.core.core;

/**
 * 环境基类
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {

	/**
	 * default prop
	 */
	public static final String DEFAULT_PROP = "xxl-conf.properties";

	/**
	 * prop file location, if not empty this file will be replaced with this disk file, like "file:/data/webapps/xxl-conf.properties" or "xxl-conf02.properties"
	 */
	public static final String PROP_FILE_LOCATION = "xxl.conf.prop.file.location";

	/**
	 * conf data path in zk
     */
	public static final String CONF_DATA_PATH = "/xxl-conf";


	/**
	 * zk address, as "ip1:port,ip2:port"
     */
	public static final String ZK_ADDRESS = "xxl.conf.zkserver";		// zk地址：格式

}

