package com.xxl.conf.example.controller;

import com.xxl.conf.core.XxlCfgLocalCache;
import com.xxl.conf.example.core.constant.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
	
	@Autowired
	private Configuration config1;
	
	@RequestMapping("")
	@ResponseBody
	public List<Configuration> index(){
		
		Configuration config2 = new Configuration();
		config2.setKey01(XxlCfgLocalCache.get("xxl-conf-demo.key01", null));
		config2.setKey02(XxlCfgLocalCache.get("xxl-conf-demo.key02", null));
		config2.setKey03(XxlCfgLocalCache.get("xxl-conf-demo.key03", null));
		
		List<Configuration> list = new ArrayList<Configuration>();
		list.add(config1);
		list.add(config2);
		return list;
	}
}
