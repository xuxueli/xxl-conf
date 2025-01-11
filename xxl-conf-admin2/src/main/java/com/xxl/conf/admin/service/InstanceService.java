package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.dto.InstanceDTO;
import com.xxl.conf.admin.model.entity.Instance;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* Instance Service
*
* Created by xuxueli on '2024-12-07 21:44:18'.
*/
public interface InstanceService {

    /**
    * 新增
    */
    public Response<String> insert(Instance instance);

    /**
    * 删除
    */
    public Response<String> delete(List<Integer> ids);

    /**
    * 更新
    */
    public Response<String> update(Instance instance);

    /**
    * Load查询
    */
    public Response<Instance> load(int id);

    /**
    * 分页查询
    */
    public PageModel<InstanceDTO> pageList(int offset, int pagesize, String appname, String env);

}
