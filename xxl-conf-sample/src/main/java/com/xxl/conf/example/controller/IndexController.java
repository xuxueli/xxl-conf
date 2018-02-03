package com.xxl.conf.example.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.example.core.constant.DemoConf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author xuxueli 2018-02-04 01:27:30
 */
@Controller
public class IndexController {
	
	@Resource
	private DemoConf demoConf;

	@RequestMapping("")
	public String index(Model model){

        /**
         * 方式1: XML占位符方式
         *
		 * 		- 参考 "applicationcontext-xxl-conf.xml" 中 "DemoConf.paramByXml" 属性配置 ""；示例代码 "<property name="paramByXml" value="${default.key01}" />"；
		 * 		- 用法：占位符方式 "${default.key01}"，支持嵌套占位符；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 			- 支持嵌套占位符；
		 * 		- 缺点：不支持支持动态推送更新
         *
         */
		model.addAttribute("key01", demoConf.paramByXml);

		/**
		 * 方式2: @Value注解方式
		 *
		 * 		- 参考 "IndexController.paramByAnno" 属性配置；示例代码 "@XxlConf("default.key02") public String paramByAnno;"；
		 * 		- 用法：对象Field上加注解 ""@XxlConf("default.key02")"，支持设置默认值，支持设置是否开启动态刷新；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 			- 支持设置配置默认值；
		 * 			- 支持设置是否开启动态刷新（TODO ,实现中）
		 */
		model.addAttribute("key02", demoConf.paramByAnno);

        /**
         * 方式3: API方式
         *
		 * 		- 参考 "IndexController" 中 "XxlConfClient.get("key", null)" 即可；
		 * 		- 用法：代码中直接调用API即可，示例代码 ""XxlConfClient.get("key", null)"";
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 支持动态推送更新；
		 * 			- 支持多数据类型；
         */
		model.addAttribute("key03", XxlConfClient.get("default.key03", null));

		return "index";
	}
}
