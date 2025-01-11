package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ConfDataMapper;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.conf.admin.service.ConfDataService;
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
* ConfData Service Impl
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Service
public class ConfDataServiceImpl implements ConfDataService {

	@Resource
	private ConfDataMapper confDataMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(ConfData confData) {

		// valid
		if (confData == null) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }

		confDataMapper.insert(confData);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = confDataMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(ConfData confData) {
		int ret = confDataMapper.update(confData);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<ConfData> load(int id) {
		ConfData record = confDataMapper.load(id);
		return new ResponseBuilder<ConfData>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<ConfData> pageList(int offset, int pagesize) {

		List<ConfData> pageList = confDataMapper.pageList(offset, pagesize);
		int totalCount = confDataMapper.pageListCount(offset, pagesize);

		// result
		PageModel<ConfData> pageModel = new PageModel<ConfData>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
