package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.Environment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* Environment Mapper
*
* Created by xuxueli on '2024-12-07 15:40:35'.
*/
@Mapper
public interface EnvironmentMapper {

    /**
    * 新增
    */
    public int insert(@Param("environment") Environment environment);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("environment") Environment environment);

    /**
    * Load查询
    */
    public Environment load(@Param("id") int id);

    /**
     * Load查询 with env
     */
    public Environment loadByEnv(@Param("env") String env);

    /**
    * 分页查询Data
    */
	public List<Environment> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("env") String env,
                                      @Param("name") String name);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("env") String env,
                             @Param("name") String name);

    /**
     * find all
     */
    List<Environment> findAll();
}
