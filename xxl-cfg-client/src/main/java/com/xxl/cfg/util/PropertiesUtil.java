package com.xxl.cfg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties工具类
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {
	protected static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	/**
	 * 加载Properties
	 * @param propertyFileName
	 * @param ifClassPath
	 * @return
	 */
	public static Properties loadProperties(String propertyFileName, boolean ifClassPath) {
		Properties prop = new Properties();
		InputStreamReader  in = null;
		try {
			URL url = null;	// 方式2：配置更新不需要重启JVM
			if (ifClassPath) {
				ClassLoader loder = Thread.currentThread().getContextClassLoader();
				url = loder.getResource(propertyFileName); 
			} else {
				url = new File(propertyFileName).toURI().toURL();
			}
			if (url != null) {
				in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
			}
			if (in != null) {
				prop.load(in);
			}
		} catch (IOException e) {
			logger.error("load {} error!", propertyFileName);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close {} error!", propertyFileName);
				}
			}
		}
		return prop;
	}

	/**
	 * 获取配置String
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getString(Properties prop, String key, String defauleValue) {
		String result = null;
		if (prop!=null) {
			result = prop.getProperty(key);
			if (result != null) {
				result = result.trim();
			}
		}
		if (StringUtils.isBlank(result) && defauleValue!=null) {
			return defauleValue;
		}
		return result;
	}

	/**
	 * 获取配置int
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	protected static int getInt(Properties prop, String key) {
		return Integer.parseInt(getString(prop, key, null));
	}

}
