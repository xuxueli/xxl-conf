package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ConfDataLogMapper;
import com.xxl.conf.admin.model.adaptor.ConfDataLogAdaptor;
import com.xxl.conf.admin.model.dto.ConfDataLogDTO;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.service.ConfDataLogService;
import com.xxl.conf.admin.service.ConfDataService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.CollectionTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* ConfDataLog Service Impl
*
* Created by xuxueli on '2025-01-11 23:08:28'.
*/
@Service
public class ConfDataLogServiceImpl implements ConfDataLogService {

	@Resource
	private ConfDataLogMapper confDataLogMapper;
    @Autowired
    private ConfDataService confDataService;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(ConfDataLog confDataLog) {

		// valid
		if (confDataLog == null) {
			return Response.ofFail("必要参数缺失");
        }

		confDataLogMapper.insert(confDataLog);
		return Response.ofSuccess();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Long> ids) {
		int ret = confDataLogMapper.delete(ids);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(ConfDataLog confDataLog) {
		int ret = confDataLogMapper.update(confDataLog);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	@Override
	public Response<String> rollback(String optUserName, long dataLogId) {
		ConfDataLog confDataLog = confDataLogMapper.load(dataLogId);
		if (confDataLog == null) {
			return Response.ofFail(I18nUtil.getString("system_param_empty"));
		}

		// do rollback
		return confDataService.updateDataValue(confDataLog.getDataId(), confDataLog.getValue(), optUserName);
	}

	/**
	* Load查询
	*/
	@Override
	public Response<ConfDataLog> load(Long id) {
		ConfDataLog record = confDataLogMapper.load(id);
		return Response.ofSuccess(record);
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<ConfDataLogDTO> pageList(int offset, int pagesize, long dataId) {

		// page list
		List<ConfDataLog> pageList = confDataLogMapper.pageList(offset, pagesize, dataId);
		int totalCount = confDataLogMapper.pageListCount(offset, pagesize, dataId);

		// adaptor
		List<ConfDataLogDTO> pageList2 = CollectionTool.isNotEmpty(pageList)
				?pageList.stream().map(ConfDataLogAdaptor::adapt).toList()
				:new ArrayList<>();

		// result
		PageModel<ConfDataLogDTO> pageModel = new PageModel<>();
		pageModel.setPageData(pageList2);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
