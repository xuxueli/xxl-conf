package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.mapper.InstanceMapper;
import com.xxl.conf.admin.model.dto.InstanceDTO;
import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.conf.admin.service.InstanceService;
import com.xxl.conf.admin.util.I18nUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.PageModel;

/**
* Instance Service Impl
*
* Created by xuxueli on '2024-12-07 21:44:18'.
*/
@Service
public class InstanceServiceImpl implements InstanceService {

	@Resource
	private InstanceMapper instanceMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(Instance instance, LoginUserDTO loginUser, boolean isAdmin) {

		// valid
		if (instance == null
				|| StringTool.isBlank(instance.getEnv())
				|| StringTool.isBlank(instance.getAppname())
				|| StringTool.isBlank(instance.getIp())
				|| instance.getPort() <=0
				|| InstanceRegisterModelEnum.match(instance.getRegisterModel())==null ){
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }

		// valid application
		List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
		if (!isAdmin && !appnameList.contains(instance.getAppname())){
			return new ResponseBuilder<String>().fail(I18nUtil.getString("system_permission_limit")).build();
		}

		// add
		instanceMapper.insert(instance);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids, LoginUserDTO loginUser, boolean isAdmin) {

		int ret = instanceMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(Instance instance, LoginUserDTO loginUser, boolean isAdmin) {

		// valid
		InstanceRegisterModelEnum registerModelEnum = InstanceRegisterModelEnum.match(instance.getRegisterModel());
		if (instance == null
				|| instance.getId() <=0
				|| registerModelEnum==null ){
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
		}

		// valid application
		List<String> appnameList = loginUser.getPermission()!=null? Arrays.asList(loginUser.getPermission().split(",")):new ArrayList<>();
		if (!isAdmin && !appnameList.contains(instance.getAppname())){
			return new ResponseBuilder<String>().fail(I18nUtil.getString("system_permission_limit")).build();
		}

		// update
		int ret = instanceMapper.update(instance);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<Instance> load(int id) {
		Instance record = instanceMapper.load(id);
		return new ResponseBuilder<Instance>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<InstanceDTO> pageList(int offset, int pagesize, String appname, String env) {

		List<Instance> pageList = instanceMapper.pageList(offset, pagesize, appname, env);
		int totalCount = instanceMapper.pageListCount(offset, pagesize, appname, env);

		// dto
		List<InstanceDTO> instanceDTOList = new ArrayList<>();
		if (CollectionTool.isNotEmpty(pageList)) {
			instanceDTOList = pageList.stream()
					.map(item -> new InstanceDTO(item))
					.collect(Collectors.toList());
		}

		// result
		PageModel<InstanceDTO> pageModel = new PageModel<>();
		pageModel.setPageData(instanceDTOList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
