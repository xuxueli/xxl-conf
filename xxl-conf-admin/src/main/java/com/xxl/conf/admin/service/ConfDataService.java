package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.entity.ConfData;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* ConfData Service
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
public interface ConfDataService {

    /**
    * 新增
    */
    public Response<String> insert(ConfData confData, LoginUserDTO loginUser, boolean isAdmin);

    /**
    * 删除
    */
    public Response<String> delete(List<Long> ids, LoginUserDTO loginUser, boolean isAdmin);

    /**
    * 更新
    */
    public Response<String> update(ConfData confData, LoginUserDTO loginUser, boolean isAdmin);

    /**
    * Load查询
    */
    public Response<ConfData> load(Long id);

    /**
    * 分页查询
    */
    public PageModel<ConfData> pageList(int offset, int pagesize, String env, String appname, String key);

}
