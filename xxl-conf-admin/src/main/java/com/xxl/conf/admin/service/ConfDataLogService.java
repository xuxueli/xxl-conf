package com.xxl.conf.admin.service;

import java.util.List;

import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;

/**
* ConfDataLog Service
*
* Created by xuxueli on '2025-01-11 23:08:28'.
*/
public interface ConfDataLogService {

    /**
    * 新增
    */
    public Response<String> insert(ConfDataLog confDataLog);

    /**
    * 删除
    */
    public Response<String> delete(List<Long> ids);

    /**
    * 更新
    */
    public Response<String> update(ConfDataLog confDataLog);

    /**
    * Load查询
    */
    public Response<ConfDataLog> load(Long id);

    /**
    * 分页查询
    */
    public PageModel<ConfDataLog> pageList(int offset, int pagesize, long dataId);

}
