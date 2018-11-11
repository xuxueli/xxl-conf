package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.core.model.*;
import com.xxl.conf.admin.core.util.ReturnT;
import com.xxl.conf.admin.dao.XxlConfEnvDao;
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
	@Resource
	private XxlConfEnvDao xxlConfEnvDao;

	@Override
	public boolean ifHasProjectPermission(XxlConfUser loginUser, String loginEnv, String appname){
		if (loginUser.getPermission() == 1) {
			return true;
		}
		if (ArrayUtils.contains(StringUtils.split(loginUser.getPermissionData(), ","), (appname.concat("#").concat(loginEnv)) )) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String,Object> pageList(int offset,
									   int pagesize,
									   String appname,
									   String key,
									   XxlConfUser loginUser,
									   String loginEnv) {

		// project permission
		if (StringUtils.isBlank(loginEnv) || StringUtils.isBlank(appname) || !ifHasProjectPermission(loginUser, loginEnv, appname)) {
			//return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("data", new ArrayList<>());
			emptyMap.put("recordsTotal", 0);
			emptyMap.put("recordsFiltered", 0);
			return emptyMap;
		}

		// xxlConfNode in mysql
		List<XxlConfNode> data = xxlConfNodeDao.pageList(offset, pagesize, loginEnv, appname, key);
		int list_count = xxlConfNodeDao.pageListCount(offset, pagesize, loginEnv, appname, key);

		// fill value in zk
		if (CollectionUtils.isNotEmpty(data)) {
			for (XxlConfNode node: data) {
				String realNodeValue = xxlConfManager.get(node.getEnv(), node.getKey());
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

	@Override
	public ReturnT<String> delete(String key, XxlConfUser loginUser, String loginEnv) {
		if (StringUtils.isBlank(key)) {
			return new ReturnT<String>(500, "参数缺失");
		}
		XxlConfNode existNode = xxlConfNodeDao.load(loginEnv, key);
		if (existNode == null) {
			return new ReturnT<String>(500, "参数非法");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, loginEnv, existNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		xxlConfManager.delete(loginEnv, key);
		xxlConfNodeDao.delete(loginEnv, key);
		xxlConfNodeLogDao.deleteTimeout(loginEnv, key, 0);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> add(XxlConfNode xxlConfNode, XxlConfUser loginUser, String loginEnv) {

		// valid
		if (StringUtils.isBlank(xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "AppName不可为空");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, loginEnv, xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		// valid group
		XxlConfProject group = xxlConfProjectDao.load(xxlConfNode.getAppname());
		if (group==null) {
			return new ReturnT<String>(500, "AppName非法");
		}

		// valid env
		if (StringUtils.isBlank(xxlConfNode.getEnv())) {
			return new ReturnT<String>(500, "配置Env不可为空");
		}
		XxlConfEnv xxlConfEnv = xxlConfEnvDao.load(xxlConfNode.getEnv());
		if (xxlConfEnv == null) {
			return new ReturnT<String>(500, "配置Env非法");
		}

		// valid key
		if (StringUtils.isBlank(xxlConfNode.getKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		xxlConfNode.setKey(xxlConfNode.getKey().trim());

		XxlConfNode existNode = xxlConfNodeDao.load(xxlConfNode.getEnv(), xxlConfNode.getKey());
		if (existNode != null) {
			return new ReturnT<String>(500, "配置Key已存在，不可重复添加");
		}
		if (!xxlConfNode.getKey().startsWith(xxlConfNode.getAppname())) {
			return new ReturnT<String>(500, "配置Key格式非法");
		}

		// valid title
		if (StringUtils.isBlank(xxlConfNode.getTitle())) {
			return new ReturnT<String>(500, "配置描述不可为空");
		}

		// value force null to ""
		if (xxlConfNode.getValue() == null) {
			xxlConfNode.setValue("");
		}

		// add node
		xxlConfManager.set(xxlConfNode.getEnv(), xxlConfNode.getKey(), xxlConfNode.getValue());
		xxlConfNodeDao.insert(xxlConfNode);

		// node log
		XxlConfNodeLog nodeLog = new XxlConfNodeLog();
		nodeLog.setEnv(xxlConfNode.getEnv());
		nodeLog.setKey(xxlConfNode.getKey());
		nodeLog.setTitle(xxlConfNode.getTitle() + "(配置新增)" );
		nodeLog.setValue(xxlConfNode.getValue());
		nodeLog.setOptuser(loginUser.getUsername());
		xxlConfNodeLogDao.add(nodeLog);

		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> update(XxlConfNode xxlConfNode, XxlConfUser loginUser, String loginEnv) {

		// valid
		if (StringUtils.isBlank(xxlConfNode.getKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		XxlConfNode existNode = xxlConfNodeDao.load(xxlConfNode.getEnv(), xxlConfNode.getKey());
		if (existNode == null) {
			return new ReturnT<String>(500, "配置Key非法");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, loginEnv, existNode.getAppname())) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		if (StringUtils.isBlank(xxlConfNode.getTitle())) {
			return new ReturnT<String>(500, "配置描述不可为空");
		}

		// value force null to ""
		if (xxlConfNode.getValue() == null) {
			xxlConfNode.setValue("");
		}

		// update conf
		xxlConfManager.set(xxlConfNode.getEnv(), xxlConfNode.getKey(), xxlConfNode.getValue());

		existNode.setTitle(xxlConfNode.getTitle());
		existNode.setValue(xxlConfNode.getValue());
		int ret = xxlConfNodeDao.update(existNode);
		if (ret < 1) {
			return ReturnT.FAIL;
		}

		// node log
		XxlConfNodeLog nodeLog = new XxlConfNodeLog();
		nodeLog.setEnv(existNode.getEnv());
		nodeLog.setKey(existNode.getKey());
		nodeLog.setTitle(existNode.getTitle() + "(配置更新)" );
		nodeLog.setValue(existNode.getValue());
		nodeLog.setOptuser(loginUser.getUsername());
		xxlConfNodeLogDao.add(nodeLog);
		xxlConfNodeLogDao.deleteTimeout(existNode.getEnv(), existNode.getKey(), 10);

		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> syncConf(String appname, XxlConfUser loginUser, String loginEnv) {

		// valid
		XxlConfEnv xxlConfEnv = xxlConfEnvDao.load(loginEnv);
		if (xxlConfEnv == null) {
			return new ReturnT<String>(500, "配置Env非法");
		}
		XxlConfProject group = xxlConfProjectDao.load(appname);
		if (group==null) {
			return new ReturnT<String>(500, "AppName非法");
		}

		// project permission
		if (!ifHasProjectPermission(loginUser, loginEnv, appname)) {
			return new ReturnT<String>(500, "您没有该项目的配置权限,请联系管理员开通");
		}

		List<XxlConfNode> confNodeList = xxlConfNodeDao.pageList(0, 10000, loginEnv, appname, null);
		if (CollectionUtils.isEmpty(confNodeList)) {
			return new ReturnT<String>(500, "操作失败，该项目下不存在配置项");
		}

		// un sync node
		List<XxlConfNode> unSyncConfNodeList = new ArrayList<>();
		for (XxlConfNode node: confNodeList) {
			String realNodeValue = xxlConfManager.get(node.getEnv(), node.getKey());
			if (!node.getValue().equals(realNodeValue)) {
				unSyncConfNodeList.add(node);
			}
		}

		if (CollectionUtils.isEmpty(unSyncConfNodeList)) {
			return new ReturnT<String>(500, "操作失败，该项目下不存未同步的配置项");
		}

		// do sync
		String logContent = "操作成功，共计同步 " + unSyncConfNodeList.size() + " 条配置：";
		for (XxlConfNode node: unSyncConfNodeList) {

			xxlConfManager.set(node.getEnv(), node.getKey(), node.getValue());

			// node log
			XxlConfNodeLog nodeLog = new XxlConfNodeLog();
			nodeLog.setEnv(node.getEnv());
			nodeLog.setKey(node.getKey());
			nodeLog.setTitle(node.getTitle() + "(全量同步)" );
			nodeLog.setValue(node.getValue());
			nodeLog.setOptuser(loginUser.getUsername());
			xxlConfNodeLogDao.add(nodeLog);
			xxlConfNodeLogDao.deleteTimeout(node.getEnv(), node.getKey(), 10);

			logContent += "<br>" + node.getKey();
		}
		logContent.substring(logContent.length() - 1);

		return new ReturnT<String>(ReturnT.SUCCESS.getCode(), logContent);
	}


}
