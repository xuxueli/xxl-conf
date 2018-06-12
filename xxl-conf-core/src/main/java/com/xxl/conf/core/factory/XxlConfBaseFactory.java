package com.xxl.conf.core.factory;

import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfMirrorConf;
import com.xxl.conf.core.core.XxlConfZkConf;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import com.xxl.conf.core.listener.impl.BeanRefreshXxlConfListener;
import com.xxl.conf.core.util.PropUtil;

import java.util.Properties;

/**
 * XxlConf Base Factory
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class XxlConfBaseFactory {

	/**
	 * init
	 *
	 * @param envprop
	 */
	public static void init(String envprop) {

		String zkaddress = null;
		String zkdigest = null;
		String env = null;
		String mirrorfile = null;

		// env prop
		if (envprop!=null && envprop.trim().length()>0) {
			Properties envPropFile = PropUtil.loadProp(envprop);
			if (envPropFile!=null && envPropFile.stringPropertyNames()!=null && envPropFile.stringPropertyNames().size()>0) {
				for (String key: envPropFile.stringPropertyNames()) {
					if ("xxl.conf.zkaddress".equals(key)) {
						zkaddress = envPropFile.getProperty(key);	// replace if envprop not exist
					} else if ("xxl.conf.zkdigest".equals(key)) {
						zkdigest = envPropFile.getProperty(key);
					} else if ("xxl.conf.env".equals(key)) {
						env = envPropFile.getProperty(key);
					} else if ("xxl.conf.mirrorfile".equals(key)) {
						mirrorfile = envPropFile.getProperty(key);
					}
				}
			}
		}


		init(zkaddress, zkdigest, env, mirrorfile);
	}

	/**
	 * init
	 *
	 * @param zkaddress
	 * @param zkdigest
	 * @param env
	 */
	public static void init(String zkaddress, String zkdigest, String env, String mirrorfile) {
		// init
		XxlConfZkConf.init(zkaddress, zkdigest, env);										// init zk client
		XxlConfMirrorConf.init(mirrorfile);													// init file mirror
		XxlConfLocalCacheConf.init();														// init cache
		XxlConfListenerFactory.addListener(null, new BeanRefreshXxlConfListener());    // listener all key change

	}

	/**
	 * destory
	 */
	public static void destroy() {
		XxlConfLocalCacheConf.destroy();	// destroy ehcache
		XxlConfZkConf.destroy();			// destroy zk client
	}

}
