package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ConfDataLogMapper;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.service.ConfDataLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
	public PageModel<ConfDataLog> pageList(int offset, int pagesize, long dataId) {

		List<ConfDataLog> pageList = confDataLogMapper.pageList(offset, pagesize, dataId);
		int totalCount = confDataLogMapper.pageListCount(offset, pagesize, dataId);

		// result
		PageModel<ConfDataLog> pageModel = new PageModel<ConfDataLog>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
