package com.xxl.conf.example.controller;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.example.core.constant.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class IndexController {
	
	@Resource
	private Configuration configuration;
	
	@RequestMapping("")
	@ResponseBody
	public Map<String, String> index(){

		String paramByClient = XxlConfClient.get("key02", null);

		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("paramByXml", configuration.paramByXml);
		map.put("paramByClient", paramByClient);

		return map;
	}
}
