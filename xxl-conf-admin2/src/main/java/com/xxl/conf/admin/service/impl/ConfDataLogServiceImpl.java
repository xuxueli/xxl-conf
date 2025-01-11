package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ConfDataLogMapper;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.conf.admin.service.ConfDataLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
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
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }

		confDataLogMapper.insert(confDataLog);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = confDataLogMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(ConfDataLog confDataLog) {
		int ret = confDataLogMapper.update(confDataLog);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<ConfDataLog> load(int id) {
		ConfDataLog record = confDataLogMapper.load(id);
		return new ResponseBuilder<ConfDataLog>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<ConfDataLog> pageList(int offset, int pagesize) {

		List<ConfDataLog> pageList = confDataLogMapper.pageList(offset, pagesize);
		int totalCount = confDataLogMapper.pageListCount(offset, pagesize);

		// result
		PageModel<ConfDataLog> pageModel = new PageModel<ConfDataLog>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
