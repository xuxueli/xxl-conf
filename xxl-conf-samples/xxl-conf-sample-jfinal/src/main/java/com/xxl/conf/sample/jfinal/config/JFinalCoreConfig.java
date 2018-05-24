package com.xxl.conf.sample.jfinal.config;

import com.jfinal.config.*;
import com.xxl.conf.core.factory.XxlConfBaseFactory;
import com.xxl.conf.sample.jfinal.controller.IndexController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-05-24
 */
public class JFinalCoreConfig extends JFinalConfig {
	private static Logger logger = LoggerFactory.getLogger(JFinalCoreConfig.class);

	// ---------------------- xxl-conf ----------------------
	private void initXxlConfFactory() {
		XxlConfBaseFactory.init("xxl-conf.properties");
	}
	private void destoryXxlConfFactory() {
		XxlConfBaseFactory.destroy();
	}

	// ---------------------- jfinal ----------------------

	public void configRoute(Routes route) {
		route.add("/", IndexController.class);
	}

	@Override
	public void afterJFinalStart() {
		initXxlConfFactory();
	}

	@Override
	public void beforeJFinalStop() {
		destoryXxlConfFactory();
	}

	public void configConstant(Constants constants) {

	}

	public void configPlugin(Plugins plugins) {

	}

	public void configInterceptor(Interceptors interceptors) {

	}

	public void configHandler(Handlers handlers) {

	}

}