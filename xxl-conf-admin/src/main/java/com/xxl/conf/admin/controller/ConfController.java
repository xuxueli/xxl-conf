package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 配置管理
 *
 * @author xuxueli
 */
@Controller
@RequestMapping("/conf")
public class ConfController {

	@Resource
	private XxlConfProjectDao xxlConfProjectDao;
	@Resource
	private IXxlConfNodeService xxlConfNodeService;
	
	@RequestMapping("")
	@PermessionLimit
	public String index(Model model, String appname){

		List<XxlConfProject> list = xxlConfProjectDao.findAll();
		if (CollectionUtils.isEmpty(list)) {
			throw new RuntimeException("系统异常，无可用项目");
		}

		XxlConfProject project = list.get(0);
		for (XxlConfProject item: list) {
			if (item.getAppname().equals(appname)) {
				project = item;
			}
		}

		model.addAttribute("ProjectList", list);
		model.addAttribute("project", project);

		return "conf/conf.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	@PermessionLimit
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String appname,
										String key) {
		return xxlConfNodeService.pageList(start, length, appname, key);
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> delete(String key){
		return xxlConfNodeService.delete(key);
	}

	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> add(XxlConfNode xxlConfNode){
		return xxlConfNodeService.add(xxlConfNode);
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> update(XxlConfNode xxlConfNode){
		return xxlConfNodeService.update(xxlConfNode);
	}
	
}
