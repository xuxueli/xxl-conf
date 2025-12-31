package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.mapper.ConfDataLogMapper;
import com.xxl.conf.admin.mapper.ConfDataMapper;
import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.openapi.registry.thread.MessageHelpler;
import com.xxl.conf.admin.service.ConfDataBizService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* ConfData Service Impl
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Service
public class ConfDataBizServiceImpl implements ConfDataBizService {

	@Resource
	private ConfDataMapper confDataMapper;
	@Resource
	private ConfDataLogMapper confDataLogMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(ConfData confData, String optUserName) {

		// valid
		if (confData == null || StringTool.isBlank(confData.getEnv()) || StringTool.isBlank(confData.getAppname())) {
			return Response.ofFail("必要参数缺失");
		}
		if (StringTool.isBlank(confData.getValue())) {
			confData.setValue("");
		}

		// opt
		confDataMapper.insert(confData);
		// log
		confDataLogMapper.insert(new ConfDataLog(confData.getId(), "", confData.getValue(), optUserName));

		// broadcast message
		MessageHelpler.broadcastMessage(MessageTypeEnum.CONFDATA, GsonTool.toJson(new MessageForConfDataDTO(confData)));

		return Response.ofSuccess();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Long> ids, String optUserName) {

		// valid
		if (CollectionTool.isEmpty(ids)) {
			return Response.ofFail("必要参数缺失");
		}

		// delete
		int ret = confDataMapper.delete(ids);

		// log
		for (Long id: ids) {
			confDataLogMapper.insert(new ConfDataLog(id, "", "Deleted", optUserName));
		}
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(ConfData confData, String optUserName) {

		// valid
		if (confData == null || StringTool.isBlank(confData.getEnv()) || StringTool.isBlank(confData.getAppname())) {
			return Response.ofFail("必要参数缺失");
		}

		// load conf-data
		ConfData confDataOld = confDataMapper.load(confData.getId());
		if (confDataOld == null) {
			return Response.ofFail("必要参数非法");
		}

		// opt
		int ret = confDataMapper.update(confData);
		if (ret > 0) {
			// log
			confDataLogMapper.insert(new ConfDataLog(confData.getId(), confDataOld.getValue(), confData.getValue(), optUserName));

			// broadcast message
			MessageHelpler.broadcastMessage(MessageTypeEnum.CONFDATA, GsonTool.toJson(new MessageForConfDataDTO(confData)));
		}
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	@Override
	public Response<String> updateDataValue(long dataId, String value, String optUserName) {
		// load conf-data
		ConfData confData = confDataMapper.load(dataId);

		// valid
		if (confData == null || StringTool.isBlank(optUserName)) {
			return Response.ofFail(I18nUtil.getString("system_param_empty"));
		}
		if (StringTool.isBlank(value)) {
			value = "";
		}

		// update value
		confData.setValue(value);
		int ret = confDataMapper.update(confData);
		if (ret > 0) {
			// log
			confDataLogMapper.insert(new ConfDataLog(confData.getId(), confData.getValue(), value, optUserName));

			// broadcast message
			MessageHelpler.broadcastMessage(MessageTypeEnum.CONFDATA, GsonTool.toJson(new MessageForConfDataDTO(confData)));
		}
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* Load查询
	*/
	@Override
	public Response<ConfData> load(Long id) {
		ConfData record = confDataMapper.load(id);
		return Response.ofSuccess(record);
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
		pageModel.setData(pageList);
		pageModel.setTotal(totalCount);

		return pageModel;
	}

}
