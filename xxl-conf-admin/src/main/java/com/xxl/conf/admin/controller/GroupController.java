package com.xxl.conf.admin.controller;

import com.xxl.conf.admin.core.model.XxlConfGroup;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.IXxlConfGroupDao;
import com.xxl.conf.admin.dao.IXxlConfNodeDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * conf group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/group")
public class GroupController {
	
	@Resource
	private IXxlConfGroupDao xxlConfGroupDao;
	@Resource
	private IXxlConfNodeDao xxlConfNodeDao;

	@RequestMapping
	public String index(Model model) {

		List<XxlConfGroup> list = xxlConfGroupDao.findAll();

		model.addAttribute("list", list);
		return "group/group.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(XxlConfGroup xxlConfGroup){

		// valid
		if (xxlConfGroup.getGroupName()==null || StringUtils.isBlank(xxlConfGroup.getGroupName())) {
			return new ReturnT<String>(500, "请输入GroupName");
		}
		if (xxlConfGroup.getGroupName().length()<4 || xxlConfGroup.getGroupName().length()>100) {
			return new ReturnT<String>(500, "GroupName长度限制为4~100");
		}
		if (xxlConfGroup.getGroupTitle()==null || StringUtils.isBlank(xxlConfGroup.getGroupTitle())) {
			return new ReturnT<String>(500, "请输入分组名称");
		}

		// valid repeat
		XxlConfGroup groupOld = xxlConfGroupDao.load(xxlConfGroup.getGroupName());
		if (groupOld!=null) {
			return new ReturnT<String>(500, "GroupName对应分组以存在,请勿重复添加");
		}

		int ret = xxlConfGroupDao.save(xxlConfGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlConfGroup xxlConfGroup){

		// valid
		if (xxlConfGroup.getGroupName()==null || StringUtils.isBlank(xxlConfGroup.getGroupName())) {
			return new ReturnT<String>(500, "请输入GroupName");
		}
		if (xxlConfGroup.getGroupName().length()<4 || xxlConfGroup.getGroupName().length()>100) {
			return new ReturnT<String>(500, "GroupName长度限制为4~100");
		}
		if (xxlConfGroup.getGroupTitle()==null || StringUtils.isBlank(xxlConfGroup.getGroupTitle())) {
			return new ReturnT<String>(500, "请输入分组名称");
		}

		int ret = xxlConfGroupDao.update(xxlConfGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String groupName){

		// valid
		int list_count = xxlConfNodeDao.pageListCount(0, 10, groupName, null);
		if (list_count > 0) {
			return new ReturnT<String>(500, "该分组使用中, 不可删除");
		}

		List<XxlConfGroup> allList = xxlConfGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, "删除失败, 系统需要至少预留一个默认分组");
		}

		int ret = xxlConfGroupDao.remove(groupName);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
