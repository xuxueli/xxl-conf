package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.dto.AccessTokenDTO;
import com.xxl.conf.admin.model.entity.AccessToken;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* AccessToken Service
*
* Created by xuxueli on '2024-12-08 16:22:29'.
*/
public interface AccessTokenService {

    /**
    * 新增
    */
    public Response<String> insert(AccessToken accessToken);

    /**
    * 删除
    */
    public Response<String> delete(List<Integer> ids);

    /**
    * 更新
    */
    public Response<String> update(AccessToken accessToken);

    /**
    * Load查询
    */
    public Response<AccessToken> load(int id);

    /**
    * 分页查询
    */
    public PageModel<AccessTokenDTO> pageList(int offset, int pagesize, String accessToken);

}
