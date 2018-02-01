package com.xxl.conf.core;

import com.xxl.conf.core.core.XxlConfException;
import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfPropConf;
import com.xxl.conf.core.core.XxlConfZkClient;

/**
 * xxl conf client
 *
 * @author xuxueli 2015-8-28 15:35:20
 */
public class XxlConfClient {

	/**
	 * get conf
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String get(String key, String defaultVal) {
		// level 1: prop conf
		String propConf = XxlConfPropConf.get(key);
		if (propConf != null) {
			return propConf;
		}

		// level 2: local cache
		XxlConfLocalCacheConf.CacheNode cacheNode = XxlConfLocalCacheConf.get(key);
		if (cacheNode != null) {
			return cacheNode.getValue();
		}

		// level 3	(get-and-watch, add-local-cache)
		String zkData = XxlConfZkClient.getPathDataByKey(key);
		XxlConfLocalCacheConf.set(key, zkData);		// cache null value
		if (zkData != null) {
			return zkData;
		}

		return defaultVal;
	}

	/**
	 * get conf (string)
	 *
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return get(key, null);
	}

	/**
	 * get conf (int)
	 *
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Integer.valueOf(value);
	}

	/**
	 * get conf (long)
	 *
	 * @param key
	 * @return
	 */
	public static long getLong(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Long.valueOf(value);
	}

	/**
	 * get conf (boolean)
	 *
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Boolean.valueOf(value);
	}

	
}
