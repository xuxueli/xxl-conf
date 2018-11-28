package com.xxl.conf.core;

import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerFactory;

/**
 * xxl conf client
 *
 * @author xuxueli 2015-8-28 15:35:20
 */
public class XxlConfClient {

	public static String get(String key, String defaultVal) {
		return XxlConfLocalCacheConf.get(key, defaultVal);
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
	 * get conf (short)
	 *
	 * @param key
	 * @return
	 */
	public static short getShort(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Short.valueOf(value);
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
	 * get conf (float)
	 *
	 * @param key
	 * @return
	 */
	public static float getFloat(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Float.valueOf(value);
	}

	/**
	 * get conf (double)
	 *
	 * @param key
	 * @return
	 */
	public static double getDouble(String key) {
		String value = get(key, null);
		if (value == null) {
			throw new XxlConfException("config key [" + key + "] does not exist");
		}
		return Double.valueOf(value);
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
