package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.entity.Application;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* Application Service
*
* Created by xuxueli on '2024-12-07 16:55:27'.
*/
public interface ApplicationService {

    /**
    * 新增
    */
    public Response<String> insert(Application application);

    /**
    * 删除
    */
    public Response<String> delete(List<Integer> ids);

    /**
    * 更新
    */
    public Response<String> update(Application application);

    /**
    * Load查询
    */
    public Response<Application> load(int id);

    /**
    * 分页查询
    */
    public PageModel<Application> pageList(int offset, int pagesize);

    /**
     * find all
     */
    Response<List<Application>> findAll();

}
