package com.xxl.conf.admin.service.impl;

import com.xxl.conf.admin.mapper.AccessTokenMapper;
import com.xxl.conf.admin.model.dto.AccessTokenDTO;
import com.xxl.conf.admin.model.entity.AccessToken;
import com.xxl.conf.admin.service.AccessTokenService;
import com.xxl.tool.core.CollectionTool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* AccessToken Service Impl
*
* Created by xuxueli on '2024-12-08 16:22:29'.
*/
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

	@Resource
	private AccessTokenMapper accessTokenMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(AccessToken accessToken) {

		// valid
		if (accessToken == null) {
			return Response.ofFail("必要参数缺失");
        }

		accessTokenMapper.insert(accessToken);
		return Response.ofSuccess();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = accessTokenMapper.delete(ids);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(AccessToken accessToken) {
		int ret = accessTokenMapper.update(accessToken);
		return ret>0? Response.ofSuccess() : Response.ofFail();
	}

	/**
	* Load查询
	*/
	@Override
	public Response<AccessToken> load(int id) {
		AccessToken record = accessTokenMapper.load(id);
		return Response.ofSuccess(record);
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<AccessTokenDTO> pageList(int offset, int pagesize, String accessToken) {

		List<AccessToken> pageList = accessTokenMapper.pageList(offset, pagesize, accessToken);
		int totalCount = accessTokenMapper.pageListCount(offset, pagesize, accessToken);

		// dto
		List<AccessTokenDTO> accessTokenDTOList = new ArrayList<>();
		if (CollectionTool.isNotEmpty(pageList)) {
			accessTokenDTOList = pageList.stream().map(AccessTokenDTO::new).collect(Collectors.toList());
		}

		// result
		PageModel<AccessTokenDTO> pageModel = new PageModel<>();
		pageModel.setPageData(accessTokenDTOList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
