package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.EnvironmentMapper;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.tool.core.StringTool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.PageModel;

/**
* Environment Service Impl
*
* Created by xuxueli on '2024-12-07 15:40:35'.
*/
@Service
public class EnvironmentServiceImpl implements EnvironmentService {

	@Resource
	private EnvironmentMapper environmentMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(Environment environment) {

		// valid
		if (environment == null
				|| StringTool.isBlank(environment.getEnv())
				|| StringTool.isBlank(environment.getName())
				|| StringTool.isBlank(environment.getDesc())) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }

		// valid
		if (environmentMapper.loadByEnv(environment.getEnv()) != null) {
			return new ResponseBuilder<String>().fail("Env（环境标识）已存在，请更换").build();
		};

		// invoke
		environmentMapper.insert(environment);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = environmentMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(Environment environment) {

		// valid
		if (environment == null
				|| StringTool.isBlank(environment.getEnv())
				|| StringTool.isBlank(environment.getName())
				|| StringTool.isBlank(environment.getDesc())) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
		}

		// invoke
		int ret = environmentMapper.update(environment);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<Environment> load(int id) {
		Environment record = environmentMapper.load(id);
		return new ResponseBuilder<Environment>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<Environment> pageList(int offset, int pagesize, String env, String name) {

		List<Environment> pageList = environmentMapper.pageList(offset, pagesize, env, name);
		int totalCount = environmentMapper.pageListCount(offset, pagesize, env, name);

		// result
		PageModel<Environment> pageModel = new PageModel<Environment>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

	@Override
	public Response<List<Environment>> findAll() {
		List<Environment> environmentList = environmentMapper.findAll();
		return new ResponseBuilder<List<Environment>>().success(environmentList).build();
	}

}