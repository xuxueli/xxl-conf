package com.xxl.conf.core.factory;

import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfMirrorConf;
import com.xxl.conf.core.core.XxlConfRemoteConf;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import com.xxl.conf.core.listener.impl.BeanRefreshXxlConfListener;

/**
 * XxlConf Base Factory
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class XxlConfBaseFactory {


	/**
	 * init
	 *
	 * @param adminAddress
	 * @param env
	 */
	public static void init(String adminAddress, String env, String accessToken, String mirrorfile) {
		// init
		XxlConfRemoteConf.init(adminAddress, env, accessToken);	// init remote util
		XxlConfMirrorConf.init(mirrorfile);			// init mirror util
		XxlConfLocalCacheConf.init();				// init cache + thread, cycle refresh + monitor

		XxlConfListenerFactory.addListener(null, new BeanRefreshXxlConfListener());    // listener all key change

	}

	/**
	 * destory
	 */
	public static void destroy() {
		XxlConfLocalCacheConf.destroy();	// destroy
	}

}
