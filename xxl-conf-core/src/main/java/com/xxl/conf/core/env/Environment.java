package com.xxl.conf.core.env;

import com.xxl.conf.core.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * xxl conf enviroment
 *
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {
	private static Logger logger = LoggerFactory.getLogger(Environment.class);

	/**
	 * env prop
	 */
	public static final String ENV_PROP = "xxl-conf.properties";

	// env param
	/**
	 * local prop (本地配置：优先加载该 "本地配置文件" 中的配置数据，其次加载配置中心中配置数据)
	 */
	public static String LOCAL_PROP;
	public static String ZK_ADDRESS;
	public static String ZK_PATH;

	private static void init() {
		// env prop
		Properties envProp = PropUtil.loadProp(Environment.ENV_PROP);
		String newEnvProp = envProp.getProperty("xxl.conf.envprop.location");	// like "xxl-conf.properties" or "file:/data/webapps/xxl-conf.properties"
		if (newEnvProp!=null && newEnvProp.trim().length()>0) {
			envProp = PropUtil.loadProp(newEnvProp);
		}

		// env param
		LOCAL_PROP = envProp.getProperty("xxl.conf.localprop.location", "xxl-conf-local.properties");
		ZK_ADDRESS = envProp.getProperty("xxl.conf.zkaddress", "127.0.0.1:2181");
		ZK_PATH = envProp.getProperty("xxl.conf.zkpath", "/xxl-conf");

		logger.info(">>>>>>>>>> xxl-conf, Environment init success. [ENV_PROP={}, LOCAL_PROP={}, ZK_ADDRESS={}, ZK_PATH={}]", newEnvProp, LOCAL_PROP, ZK_ADDRESS, ZK_PATH);
	}

	static {
		init();
	}

}

