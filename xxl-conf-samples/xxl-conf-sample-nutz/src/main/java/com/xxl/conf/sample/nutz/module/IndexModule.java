package com.xxl.conf.sample.nutz.module;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-05-24
 */
@IocBean
public class IndexModule {
	private static Logger logger = LoggerFactory.getLogger(IndexModule.class);

	static {
		/**
		 * 配置变更监听示例：可开发Listener逻辑，监听配置变更事件；可据此实现动态刷新JDBC连接池等高级功能；
		 */
		XxlConfClient.addListener("default.key01", new XxlConfListener(){
			@Override
			public void onChange(String key, String value) throws Exception {
				logger.info("配置变更事件通知：{}={}", key, value);
			}
		});
	}

	
	@At("/")
	@Ok("json")
	public String index() {

		String paramByApi = XxlConfClient.get("default.key01", null);
		String result = "API方式: default.key01=" + paramByApi;

		return result;
	}

}
