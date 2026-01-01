package com.xxl.conf.core;

import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import com.xxl.conf.core.listener.XxlConfListener;

/**
 * xxl conf client
 *
 * @author xuxueli 2015-8-28 15:35:20
 */
public class XxlConfHelper {

	// ---------------------- string type ----------------------

	/**
	 * get conf data
	 *
	 * @param appname
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String get(String appname, String key, String defaultVal) {
		return XxlConfBootstrap.getInstance().getLocalCacheHelper().get(appname, key, defaultVal);
	}

	/**
	 * get conf data
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String get(String key, String defaultVal) {
		return get(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf data
	 *
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return get(key, null);
	}


	// ---------------------- boolean type ----------------------

	/**
	 * get conf (boolean)
	 *
	 * @param key
	 * @return
	 */
	public static Boolean getBoolean(String appname, String key, Boolean defaultVal) {
		String value = get(appname, key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Boolean.valueOf(value);
	}
	public static boolean getBoolean(String key, Boolean defaultVal) {
		return getBoolean(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}
	public static boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	// ---------------------- int type ----------------------

	/**
	 * get conf (int)
	 *
	 * @param key
	 * @return
	 */
	public static Integer getInteger(String appname, String key, Integer defaultVal) {
		String value = get(key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Integer.valueOf(value);
	}
	/**
	 * get conf (int)
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Integer getInteger(String key, Integer defaultVal) {
		return getInteger(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf (int)
	 * @param key
	 * @return
	 */
	public static Integer getInteger(String key) {
		return getInteger(key, null);
	}

	// ---------------------- short type ----------------------

	/**
	 * get conf (short)
	 *
	 * @param key
	 * @return
	 */
	public static Short getShort(String appname, String key, Short defaultVal) {
		String value = get(key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Short.valueOf(value);
	}

	/**
	 * get conf (short)
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Short getShort(String key, Short defaultVal) {
		return getShort(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf (short)
	 * @param key
	 * @return
	 */
	public static Short getShort(String key) {
		return getShort(key, null);
	}

	// ---------------------- long type ----------------------

	/**
	 * get conf (long)
	 *
	 * @param key
	 * @return
	 */
	public static Long getLong(String appname, String key, Long defaultVal) {
		String value = get(key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Long.valueOf(value);
	}

	/**
	 * get conf (long)
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Long getLong(String key, Long defaultVal) {
		return getLong(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf (long)
	 * @param key
	 * @return
	 */
	public static Long getLong(String key) {
		return getLong(key, null);
	}

	// ---------------------- double type ----------------------

	/**
	 * get conf (double)
	 *
	 * @param key
	 * @return
	 */
	public static Double getDouble(String appname, String key, Double defaultVal) {
		String value = get(key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Double.valueOf(value);
	}

	/**
	 * get conf (double)
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Double getDouble(String key, Double defaultVal) {
		return getDouble(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf (double)
	 * @param key
	 * @return
	 */
	public static Double getDouble(String key) {
		return getDouble(key, null);
	}

	// ---------------------- Float type ----------------------

	/**
	 * get conf (Float)
	 *
	 * @param key
	 * @return
	 */
	public static Float getFloat(String appname, String key, Float defaultVal) {
		String value = get(key, null);
		if (value==null || value.trim().isEmpty()) {
			return defaultVal;
		}
		return Float.valueOf(value);
	}

	/**
	 * get conf (double)
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Float getFloat(String key, Float defaultVal) {
		return getFloat(XxlConfBootstrap.getInstance().getAppname(), key, defaultVal);
	}

	/**
	 * get conf (double)
	 * @param key
	 * @return
	 */
	public static Float getFloat(String key) {
		return getFloat(key, null);
	}


	// ---------------------- listener ----------------------

	/**
	 * add listener
	 *
	 * @param key
	 * @param xxlConfListener
	 * @return
	 */
	public static boolean addListener(String appname, String key, XxlConfListener xxlConfListener){
		return XxlConfBootstrap.getInstance().getListenerHelper().addListener(appname, key, xxlConfListener);
	}

	/**
	 * add listener
	 *
	 * @param key
	 * @param xxlConfListener
	 * @return
	 */
	public static boolean addListener(String key, XxlConfListener xxlConfListener){
		return addListener(XxlConfBootstrap.getInstance().getAppname(), key, xxlConfListener);
	}

}
