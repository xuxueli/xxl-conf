package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import com.xxl.conf.admin.service.impl.LoginService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String env,
										String appname,
										String key) {
		XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		return xxlConfNodeService.pageList(start, length, env, appname, key, loginUser);
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public ReturnT<String> delete(HttpServletRequest request, String env, String key){
		XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		return xxlConfNodeService.delete(env, key, loginUser);
	}

	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(HttpServletRequest request, XxlConfNode xxlConfNode){
		XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		return xxlConfNodeService.add(xxlConfNode, loginUser);
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(HttpServletRequest request, XxlConfNode xxlConfNode){
		XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		return xxlConfNodeService.update(xxlConfNode, loginUser);
	}

	@RequestMapping("/syncConf")
	@ResponseBody
	public ReturnT<String> syncConf(HttpServletRequest request,
										String env,
										String appname) {
		XxlConfUser loginUser = (XxlConfUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		return xxlConfNodeService.syncConf(env, appname, loginUser);
	}

}
