package com.xxl.conf.core;

import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfPropConf;
import com.xxl.conf.core.core.XxlConfZkConf;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerFactory;

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
		String zkData = XxlConfZkConf.get(key);
		XxlConfLocalCacheConf.set(key, zkData, "SET");		// support cache null value
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

	/**
	 * add listener with xxl conf change
	 *
	 * @param key
	 * @param xxlConfListener
	 * @return
	 */
	public static boolean addListener(String key, XxlConfListener xxlConfListener){
		return XxlConfListenerFactory.addListener(key, xxlConfListener);
	}
	
}
