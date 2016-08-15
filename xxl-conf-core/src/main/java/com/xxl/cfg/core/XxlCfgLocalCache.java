package com.xxl.cfg.core;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * local cache
 * @author xuxueli 2015-8-28 15:35:20
 */
public class XxlCfgLocalCache {
	private static Logger logger = LoggerFactory.getLogger(XxlCfgLocalCache.class);
	private static ConcurrentMap<String, String> cachedConfig = new ConcurrentHashMap<String, String>();
	private static Properties localProp;
	
	/**
	 * add/update local config by zk
	 * @param znodeKey
	 * @param znodeValue
	 */
	public static void put(String znodeKey, String znodeValue){
		cachedConfig.put(znodeKey, znodeValue);
	}
	
	/**
	 * remove local config by zk
	 * @param znodeKey
	 */
	public static void remove(String znodeKey){
		cachedConfig.remove(znodeKey);
	}
	
	/**
	 * init local prop
	 * @param localProp
	 */
	public static void setLocalProp(Properties prop) {
		localProp = prop;
	}

	/**
	 * get data by local cache >> zk
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String get(String key, String defauleValue){
		String cacheValue = null;
		if (localProp != null && localProp.containsKey(key)) {
			// local prop 1st
			cacheValue = (String) localProp.get(key);
			return cacheValue;
		} else if (cachedConfig.containsKey(key)) {
			// local cache 2nd
			cacheValue = cachedConfig.get(key);
			return cacheValue;
		} else {
			// remote zk 3rd
			cacheValue = XxlCfgClient.client.getData(key);
			logger.info(">>>>>>>>>> local cache is not found, getDate from zookeeper:[key:{}, value:{}]", key, cacheValue);
			if (StringUtils.isNotBlank(cacheValue)) {
				cachedConfig.put(key, cacheValue);
			}
		}
		if (cacheValue == null) {
			cacheValue = defauleValue;
		}
		return cacheValue;
	}
	
}
