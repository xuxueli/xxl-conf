package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.Application;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* Application Mapper
*
* Created by xuxueli on '2024-12-07 16:55:27'.
*/
@Mapper
public interface ApplicationMapper {

    /**
    * 新增
    */
    public int insert(@Param("application") Application application);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("application") Application application);

    /**
    * Load查询
    */
    public Application load(@Param("id") int id);

    /**
     * Load查询 with appname
     */
    public Application loadByAppName(@Param("appname") String appname);

    /**
    * 分页查询Data
    */
	public List<Application> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize);

    /**
     * find all
     */
    public List<Application> findAll();

}
