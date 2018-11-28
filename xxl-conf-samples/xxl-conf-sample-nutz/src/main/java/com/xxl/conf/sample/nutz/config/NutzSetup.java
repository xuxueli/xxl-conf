package com.xxl.conf.sample.nutz.config;

import com.xxl.conf.core.factory.XxlConfBaseFactory;
import com.xxl.conf.core.util.PropUtil;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * nutz setup
 *
 * @author xuxueli 2018-05-24
 */
public class NutzSetup implements Setup {
	private Logger logger = LoggerFactory.getLogger(NutzSetup.class);

	@Override
	public void init(NutConfig cfg) {
		Properties prop = PropUtil.loadProp("xxl-conf.properties");

		XxlConfBaseFactory.init(
				prop.getProperty("xxl.conf.admin.address"),
				prop.getProperty("xxl.conf.env"),
				prop.getProperty("xxl.conf.access.token"),
				prop.getProperty("xxl.conf.mirrorfile"));
	}

	@Override
	public void destroy(NutConfig cfg) {
		XxlConfBaseFactory.destroy();
	}

}
