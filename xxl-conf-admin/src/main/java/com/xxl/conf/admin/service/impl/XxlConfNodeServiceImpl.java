package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.core.model.XxlConfNode;
import com.xxl.conf.admin.core.model.XxlConfNodeLog;
import com.xxl.conf.admin.core.model.XxlConfProject;
import com.xxl.conf.admin.core.model.XxlConfUser;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfNodeDao;
import com.xxl.conf.admin.dao.XxlConfNodeLogDao;
import com.xxl.conf.admin.dao.XxlConfProjectDao;
import com.xxl.conf.admin.service.IXxlConfNodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置
 * @author xuxueli 2016-08-15 22:53
 */
@Service
public class XxlConfNodeServiceImpl implements IXxlConfNodeService {


	@Resource
	private XxlConfNodeDao xxlConfNodeDao;
	@Resource
	private XxlConfProjectDao xxlConfProjectDao;
	@Resource
	private XxlConfManager xxlConfManager;
	@Resource
	private XxlConfNodeLogDao xxlConfNodeLogDao;

	@Override
	public Map<String,Object> pageList(int offset, int pagesize, String appname, String key, XxlConfUser loginUser) {

		// project permission
		if (StringUtils.isBlank(appname) || !ifHasProjectPermission(loginUser, appname)) {
			//return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("data", new ArrayList<>());
			emptyMap.put("recordsTotal", 0);
			emptyMap.put("recordsFiltered", 0);
			return emptyMap;
		}

		// xxlConfNode in mysql
		List<XxlConfNode> data = xxlConfNodeDao.pageList(offset, pagesize, appname, key);
		int list_count = xxlConfNodeDao.pageListCount(offset, pagesize, appname, key);

		// fill value in zk
		if (CollectionUtils.isNotEmpty(data)) {
			for (XxlConfNode node: data) {
				String realNodeValue = xxlConfManager.get(node.getKey());
				node.setZkValue(realNodeValue);
			}
		}

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("data", data);
		maps.put("recordsTotal", list_count);		// 总记录数
		maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
		return maps;

	}

	private boolean ifHasProjectPermission(XxlConfUser loginUser, String appname){
		if (loginUser.getPermission() == 1) {
			return true;
		}
		if (ArrayUtils.contains(StringUtils.split(loginUser.getPermissionProjects(), ","), appname)) {
			return true;
		}
		return false;
	}

	@Override
	public ReturnT<String> delete(String key, XxlConfUser loginUser) {
		if (StringUtils.isBlank(key)) {
			return new ReturnT<String>(500, "参数缺失");
		}
		XxlConfNode existNode = xxlConfNodeDao.load(key);
		if (existNode == null) {
			return new ReturnT<String>(500, "参数非法");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, existNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		xxlConfManager.delete(key);
		xxlConfNodeDao.delete(key);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> add(XxlConfNode xxlConfNode, XxlConfUser loginUser) {

		// valid
		if (StringUtils.isBlank(xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "AppName不可为空");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		XxlConfProject group = xxlConfProjectDao.load(xxlConfNode.getAppname());
		if (group==null) {
			return new ReturnT<String>(500, "AppName非法");
		}

		if (StringUtils.isBlank(xxlConfNode.getKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		XxlConfNode existNode = xxlConfNodeDao.load(xxlConfNode.getKey());
		if (existNode != null) {
			return new ReturnT<String>(500, "配置Key已存在，不可重复添加");
		}
		if (!xxlConfNode.getKey().startsWith(xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "配置Key格式非法");
		}
		if (StringUtils.isBlank(xxlConfNode.getTitle())) {
			return new ReturnT<String>(500, "配置描述不可为空");
		}

		// value force null to ""
		if (xxlConfNode.getValue() == null) {
			xxlConfNode.setValue("");
		}

		xxlConfManager.set(xxlConfNode.getKey(), xxlConfNode.getValue());
		xxlConfNodeDao.insert(xxlConfNode);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> update(XxlConfNode xxlConfNode, XxlConfUser loginUser) {

		// valid
		if (StringUtils.isBlank(xxlConfNode.getKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		XxlConfNode existNode = xxlConfNodeDao.load(xxlConfNode.getKey());
		if (existNode == null) {
			return new ReturnT<String>(500, "配置Key非法");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, existNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}


		if (StringUtils.isBlank(xxlConfNode.getTitle())) {
			return new ReturnT<String>(500, "配置描述不可为空");
		}

		// value force null to ""
		if (xxlConfNode.getValue() == null) {
			xxlConfNode.setValue("");
		}

		xxlConfManager.set(xxlConfNode.getKey(), xxlConfNode.getValue());

		existNode.setTitle(xxlConfNode.getTitle());
		existNode.setValue(xxlConfNode.getValue());
		int ret = xxlConfNodeDao.update(existNode);
		if (ret < 1) {
			return ReturnT.FAIL;
		}

		// node log
		XxlConfNodeLog nodeLog = new XxlConfNodeLog();
		nodeLog.setKey(existNode.getKey());
		nodeLog.setTitle(existNode.getTitle());
		nodeLog.setValue(existNode.getValue());
		nodeLog.setOptuser(loginUser.getUsername());
		xxlConfNodeLogDao.add(nodeLog);
		xxlConfNodeLogDao.deleteTimeout(existNode.getKey(), 10);

		return ReturnT.SUCCESS;
	}

}
