package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.controller.annotation.PermessionLimit;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfNodeDao;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目管理
 *
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
	
	@Resource
	private XxlConfProjectDao xxlConfProjectDao;
	@Resource
	private XxlConfNodeDao xxlConfNodeDao;

	@RequestMapping
	@PermessionLimit(adminuser = true)
	public String index(Model model) {

		List<XxlConfProject> list = xxlConfProjectDao.findAll();
		model.addAttribute("list", list);

		return "project/project.index";
	}

	@RequestMapping("/save")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> save(XxlConfProject xxlConfProject){

		// valid
		if (StringUtils.isBlank(xxlConfProject.getAppname())) {
			return new ReturnT<String>(500, "AppName不可为空");
		}
		if (xxlConfProject.getAppname().length()<4 || xxlConfProject.getAppname().length()>100) {
			return new ReturnT<String>(500, "Appname长度限制为4~100");
		}
		if (StringUtils.isBlank(xxlConfProject.getTitle())) {
			return new ReturnT<String>(500, "请输入项目名称");
		}

		// valid repeat
		XxlConfProject existProject = xxlConfProjectDao.load(xxlConfProject.getAppname());
		if (existProject != null) {
			return new ReturnT<String>(500, "Appname已存在，请勿重复添加");
		}

		int ret = xxlConfProjectDao.save(xxlConfProject);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> update(XxlConfProject xxlConfProject){

		// valid
		if (StringUtils.isBlank(xxlConfProject.getAppname())) {
			return new ReturnT<String>(500, "AppName不可为空");
		}
		if (StringUtils.isBlank(xxlConfProject.getTitle())) {
			return new ReturnT<String>(500, "请输入项目名称");
		}

		int ret = xxlConfProjectDao.update(xxlConfProject);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@PermessionLimit(adminuser = true)
	@ResponseBody
	public ReturnT<String> remove(String appname){

		if (StringUtils.isBlank(appname)) {
			return new ReturnT<String>(500, "参数AppName非法");
		}

		// valid
		int list_count = xxlConfNodeDao.pageListCount(0, 10, null, appname, null);
		if (list_count > 0) {
			return new ReturnT<String>(500, "拒绝删除，该项目下存在配置数据");
		}

		List<XxlConfProject> allList = xxlConfProjectDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, "拒绝删除, 需要至少预留一个项目");
		}

		int ret = xxlConfProjectDao.delete(appname);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
