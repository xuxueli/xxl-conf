package com.xxl.conf.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.mapper.ConfDataLogMapper;
import com.xxl.conf.admin.mapper.ConfDataMapper;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.openapi.registry.thread.MessageHelpler;
import com.xxl.conf.admin.service.ConfDataService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.PageModel;

/**
* ConfData Service Impl
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Service
public class ConfDataServiceImpl implements ConfDataService {

	@Resource
	private ConfDataMapper confDataMapper;
	@Resource
	private ConfDataLogMapper confDataLogMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(ConfData confData, LoginUserDTO loginUser, boolean isAdmin) {

		// valid
		if (confData == null || StringTool.isBlank(confData.getEnv()) || StringTool.isBlank(confData.getAppname())) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
		}

		// valid application
		List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
		if (!isAdmin && !appnameList.contains(confData.getAppname())){
			return new ResponseBuilder<String>().fail(I18nUtil.getString("system_permission_limit")).build();
		}

		// opt
		confDataMapper.insert(confData);
		// log
		confDataLogMapper.insert(new ConfDataLog(confData.getId(), confData.getValue(), loginUser.getUsername()));

		// broadcast message
		MessageHelpler.broadcastMessage(MessageTypeEnum.CONFDATA, JSON.toJSONString(new MessageForConfDataDTO(confData)));

		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Long> ids, LoginUserDTO loginUser, boolean isAdmin) {

		// valid
		if (CollectionTool.isEmpty(ids)) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
		}

		// delete
		int ret = confDataMapper.delete(ids);
		// log
		for (Long id: ids) {
			confDataLogMapper.insert(new ConfDataLog(id, "", loginUser.getUsername()));
		}
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(ConfData confData, LoginUserDTO loginUser, boolean isAdmin) {

		// valid
		if (confData == null || StringTool.isBlank(confData.getEnv()) || StringTool.isBlank(confData.getAppname())) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
		}

		// valid application
		List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
		if (!isAdmin && !appnameList.contains(confData.getAppname())){
			return new ResponseBuilder<String>().fail(I18nUtil.getString("system_permission_limit")).build();
		}

		// opt
		int ret = confDataMapper.update(confData);
		if (ret > 0) {
			// log
			confDataLogMapper.insert(new ConfDataLog(confData.getId(), confData.getValue(), loginUser.getUsername()));

			// broadcast message
			MessageHelpler.broadcastMessage(MessageTypeEnum.CONFDATA, JSON.toJSONString(new MessageForConfDataDTO(confData)));
		}
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<ConfData> load(Long id) {
		ConfData record = confDataMapper.load(id);
		return new ResponseBuilder<ConfData>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<ConfData> pageList(int offset, int pagesize, String env, String appname, String key) {

		List<ConfData> pageList = confDataMapper.pageList(offset, pagesize, env, appname, key);
		int totalCount = confDataMapper.pageListCount(offset, pagesize, env, appname, key);

		// result
		PageModel<ConfData> pageModel = new PageModel<ConfData>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
