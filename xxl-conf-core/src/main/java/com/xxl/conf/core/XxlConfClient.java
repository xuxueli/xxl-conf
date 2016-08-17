package com.xxl.conf.core;

import com.xxl.conf.core.util.Environment;
import com.xxl.conf.core.util.PropertiesUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.Properties;

/**
 * local cache
 * @author xuxueli 2015-8-28 15:35:20
 */
public class XxlConfClient {

	public static Properties localProp = PropertiesUtil.loadProperties("local.properties");
	private static Cache cache;
	static {
		CacheManager manager = CacheManager.create();	// default use ehcche.xml under src
		cache = new Cache(Environment.CONF_DATA_PATH, 10000, false, true, 1800, 1800);
		manager.addCache(cache);
	}

	public static void set(String key, String value) {
		if (cache != null) {
			cache.put(new Element(key, value));
		}
	}

	public static String get(String key, String defaultVal) {
		if (localProp!=null && localProp.contains(key)) {
			return localProp.getProperty(key);
		}
		if (cache != null) {
			Element element = cache.get(key);
			if (element != null) {
				return (String) element.getObjectValue();
			}
		}
		String zkData = XxlConfZkClient.getPathDataByKey(key);
		if (zkData!=null) {
			XxlConfZkClient.setPathDataByKey(key, zkData);
			return zkData;
		}

		return defaultVal;
	}

	public static boolean remove(String key) {
		if (cache != null) {
			return cache.remove(key);
		}
		return false;
	}

	public static void main(String[] args) {
		String key = "key";
		String value = "hello";
		set(key, value);
		System.out.println(get(key, "-1"));
	}
	
}
