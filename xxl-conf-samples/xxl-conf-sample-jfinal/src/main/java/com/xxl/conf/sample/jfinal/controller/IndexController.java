package com.xxl.conf.sample.jfinal.controller;

import com.jfinal.core.Controller;
import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-05-24
 */
public class IndexController extends Controller {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

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


	public void index(){

		String paramByApi = XxlConfClient.get("default.key01", null);
		String result = "API方式: default.key01=" + paramByApi;
		renderText(result);
	}

}
