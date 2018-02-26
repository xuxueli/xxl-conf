package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.sample.demo.DemoConf;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xuxueli 2018-02-04 01:27:30
 */
@Controller
public class IndexController {
	
	@Resource
	private DemoConf demoConf;

	@RequestMapping("")
	@ResponseBody
	public List<String> index(){

		List<String> list = new LinkedList<>();

		/**
		 * 方式1: API方式
		 *
		 * 		- 参考 "IndexController" 中 "XxlConfClient.get("key", null)" 即可；
		 * 		- 用法：代码中直接调用API即可，示例代码 ""XxlConfClient.get("key", null)"";
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 			- 支持动态推送更新；
		 * 			- 支持多数据类型；
		 */
		String paramByApi = XxlConfClient.get("default.key01", null);
		list.add("1、API方式: default.key01=" + paramByApi);

		/**
		 * 方式2: @XxlConf 注解方式
		 *
		 * 		- 参考 "DemoConf.paramByAnno" 属性配置；示例代码 "@XxlConf("key") public String paramByAnno;"；
		 * 		- 用法：对象Field上加注解 ""@XxlConf("default.key02")"，支持设置默认值，支持设置是否开启动态刷新；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 			- 支持设置配置默认值；
		 * 			- 支持动态推送更新，可配置是否开启推送更新;
		 */
		list.add("2、@XxlConf 注解方式: default.key02=" + demoConf.paramByAnno);

		/**
		 * 方式3: XML占位符方式
		 *
		 * 		- 参考 "applicationcontext-xxl-conf.xml" 中 "DemoConf.paramByXml" 属性配置 ""；示例代码 "<property name="paramByXml" value="${key}" />"；
		 * 		- 用法：占位符方式 "${key}"，支持嵌套占位符；
		 * 		- 优点：
		 * 			- 配置从配置中心自动加载；
		 * 			- 存在LocalCache，不用担心性能问题；
		 * 			- 支持嵌套占位符；
		 * 		- 缺点：不支持支持动态推送更新
		 *
		 */
		list.add("3、XML占位符方式: default.key03=" + demoConf.paramByXml);

		return list;
	}

}
