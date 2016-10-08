package com.xxl.conf.example.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.example.core.constant.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class IndexController {
	
	@Resource
	private Configuration configuration;
	
	@RequestMapping("")
	@ResponseBody
	public String index(){

        /**
         * 方式1: XML文件中的占位符方式
         *
         * 说明: 配置文件 "xxl-conf/xxl-conf-example/src/main/resources/applicationcontext-xxl-conf.xml" 中configuration的配置, 其中属性paramByXml的值通过占位符方式从XXL-CONF获取;
         *
         */
        String paramByXml = configuration.paramByXml;

        /**
         * 方式2: API方式
         *
         * 说明: API方式获取, 只需要执行diamante "XxlConfClient.get("key", null)" 即可, 在业务中使用比较方便 ,而且接受XXL-CONF实时推送更新。 同时因为底层有配置缓存,并不存在性能问题;
         *
         */
		String paramByClient = XxlConfClient.get("default.key02", null);

		String result = "XML:<hr>default.key01=" + paramByXml;
		result += "<br><br><br>API:<hr>default.key02=" + paramByClient;
		return result;
	}
}
