package com.xxl.conf.example.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.example.core.constant.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class IndexController {
	
	@Resource
	private Configuration configuration;

	@Value("${default.key02}")
	private String paramByAnno;
	
	@RequestMapping("")
	public String index(Model model){

        /**
         * 方式1: XML占位符方式
         *
		 * 		- 参考 "applicationcontext-xxl-conf.xml" 中 "Configuration.paramByXml" 属性配置；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 		- 缺点：不支持支持动态推送更新
         *
         */
		model.addAttribute("key01", configuration.paramByXml);

		/**
		 * 方式2: @Value注解方式
		 *
		 * 		- 参考 "IndexController.paramByAnno" 属性配置；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 *		- 缺点：不支持支持动态推送更新
		 */
		model.addAttribute("key02", paramByAnno);

        /**
         * 方式3: API方式
         *
		 * 		- 参考 "IndexController" 中 "XxlConfClient.get("key", null)" 即可；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 支持动态推送更新；
		 *
         */
		model.addAttribute("key03", XxlConfClient.get("default.key04", null));

		return "index";
	}
}
