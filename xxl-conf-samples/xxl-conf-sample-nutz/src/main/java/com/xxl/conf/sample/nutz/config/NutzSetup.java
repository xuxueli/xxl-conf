package com.xxl.conf.sample.nutz.config;

import com.xxl.conf.core.factory.XxlConfBaseFactory;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * nutz setup
 *
 * @author xuxueli 2018-05-24
 */
public class NutzSetup implements Setup {
	private Logger logger = LoggerFactory.getLogger(NutzSetup.class);
	//public static final Log logger = Logs.get();

	@Override
	public void init(NutConfig cfg) {
		XxlConfBaseFactory.init("xxl-conf.properties");
	}

	@Override
	public void destroy(NutConfig cfg) {
		XxlConfBaseFactory.destroy();
	}

}
