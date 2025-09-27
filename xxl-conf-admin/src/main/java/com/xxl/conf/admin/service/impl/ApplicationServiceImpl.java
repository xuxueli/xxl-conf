package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.ApplicationMapper;
import com.xxl.conf.admin.model.entity.Application;
import com.xxl.conf.admin.service.ApplicationService;
import com.xxl.tool.core.StringTool;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* Application Service Impl
*
* Created by xuxueli on '2024-12-07 16:55:27'.
*/
@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Resource
	private ApplicationMapper applicationMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(Application application) {

		// valid
		if (application == null
				|| StringTool.isBlank(application.getAppname())
				|| StringTool.isBlank(application.getName())
				|| StringTool.isBlank(application.getDesc())) {
			return Response.ofFail("必要参数缺失");
        }

		// valid
		if (applicationMapper.loadByAppName(application.getAppname()) != null) {
			return Response.ofFail("AppName（环境标识）已存在，请更换");
		}

		// invoke
		applicationMapper.insert(application);
		return Response.ofSuccess();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = applicationMapper.delete(ids);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(Application application) {

		// valid
		if (application == null
				|| StringTool.isBlank(application.getAppname())
				|| StringTool.isBlank(application.getName())
				|| StringTool.isBlank(application.getDesc())) {
			return Response.ofFail("必要参数缺失");
		}

		// invoke
		int ret = applicationMapper.update(application);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* Load查询
	*/
	@Override
	public Response<Application> load(int id) {
		Application record = applicationMapper.load(id);
		return Response.ofSuccess(record);
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<Application> pageList(int offset, int pagesize, String appname, String name) {

		List<Application> pageList = applicationMapper.pageList(offset, pagesize, appname, name);
		int totalCount = applicationMapper.pageListCount(offset, pagesize, appname, name);

		// result
		PageModel<Application> pageModel = new PageModel<>();
		pageModel.setPageData(pageList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

	@Override
	public Response<List<Application>> findAll() {
		List<Application> applicationList = applicationMapper.findAll();
		return Response.ofSuccess(applicationList);
	}

}
