package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ConfDataMapper;
import com.xxl.conf.admin.mapper.EnvironmentMapper;
import com.xxl.conf.admin.mapper.InstanceMapper;
import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.conf.admin.service.EnvironmentService;
import com.xxl.tool.core.StringTool;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

import com.xxl.tool.response.Response;
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
	@Resource
	private InstanceMapper instanceMapper;
	@Resource
	private ConfDataMapper confDataMapper;

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
			return Response.ofFail("必要参数缺失");
        }

		// valid
		if (environmentMapper.loadByEnv(environment.getEnv()) != null) {
			return Response.ofFail("Env（环境标识）已存在，请更换");
		};

		// invoke
		environmentMapper.insert(environment);
		return Response.ofSuccess();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		// valid
		for (Integer id: ids) {
			Environment environment = environmentMapper.load( id);
			if (environment == null) {
				continue;
			}

			int confDataCount = confDataMapper.countByEnv(environment.getEnv());
			if (confDataCount > 0) {
				return Response.ofFail("该环境下存在配置数据，请先删除配置数据");
			}
			int instanceCount = instanceMapper.countByEnv(environment.getEnv());
			if (instanceCount > 0) {
				return Response.ofFail("该环境下存在注册节点实例，请先删除注册实例数据");
			}
		}

		// do delete
		int ret = environmentMapper.delete(ids);
		return ret>0? Response.ofSuccess() : Response.ofFail();
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
			return Response.ofFail("必要参数缺失");
		}

		// invoke
		int ret = environmentMapper.update(environment);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* Load查询
	*/
	@Override
	public Response<Environment> load(int id) {
		Environment record = environmentMapper.load(id);
		return Response.ofSuccess(record);
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
		pageModel.setData(pageList);
		pageModel.setTotal(totalCount);

		return pageModel;
	}

	@Override
	public Response<List<Environment>> findAll() {
		List<Environment> environmentList = environmentMapper.findAll();
		return Response.ofSuccess(environmentList);
	}

}
