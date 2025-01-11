package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.entity.Environment;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* Environment Service
*
* Created by xuxueli on '2024-12-07 15:40:35'.
*/
public interface EnvironmentService {

    /**
    * 新增
    */
    public Response<String> insert(Environment environment);

    /**
    * 删除
    */
    public Response<String> delete(List<Integer> ids);

    /**
    * 更新
    */
    public Response<String> update(Environment environment);

    /**
    * Load查询
    */
    public Response<Environment> load(int id);

    /**
    * 分页查询
    */
    public PageModel<Environment> pageList(int offset, int pagesize, String env, String name);

    /**
     * find all
     */
    public Response<List<Environment>> findAll();

}
