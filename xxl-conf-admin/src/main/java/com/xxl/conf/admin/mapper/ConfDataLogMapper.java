package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.ConfDataLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* ConfDataLog Mapper
*
* Created by xuxueli on '2025-01-11 23:08:28'.
*/
@Mapper
public interface ConfDataLogMapper {

    /**
    * 新增
    */
    public int insert(@Param("confDataLog") ConfDataLog confDataLog);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Long> ids);

    /**
    * 更新
    */
    public int update(@Param("confDataLog") ConfDataLog confDataLog);

    /**
    * Load查询
    */
    public ConfDataLog load(@Param("id") Long id);

    /**
    * 分页查询Data
    */
	public List<ConfDataLog> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("dataId") long dataId);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("dataId") long dataId);

}
