package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.ConfData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* ConfData Mapper
*
* Created by xuxueli on '2025-01-11 23:01:14'.
*/
@Mapper
public interface ConfDataMapper {

    /**
    * 新增
    */
    public int insert(@Param("confData") ConfData confData);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("confData") ConfData confData);

    /**
    * Load查询
    */
    public ConfData load(@Param("id") int id);

    /**
    * 分页查询Data
    */
	public List<ConfData> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize);

}
