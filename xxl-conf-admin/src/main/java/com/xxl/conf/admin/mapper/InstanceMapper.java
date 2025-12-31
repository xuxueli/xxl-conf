package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.model.entity.Instance;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
* Instance Mapper
*
* Created by xuxueli on '2024-12-07 21:44:18'.
*/
@Mapper
public interface InstanceMapper {

    /**
    * 新增
    */
    public int insert(@Param("instance") Instance instance);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("instance") Instance instance);

    /**
    * Load查询
    */
    public Instance load(@Param("id") int id);

    /**
    * 分页查询Data
    */
	public List<Instance> pageList(@Param("offset") int offset,
                                   @Param("pagesize") int pagesize,
                                   @Param("appname") String appname,
                                   @Param("env") String env);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("appname") String appname,
                             @Param("env") String env);


    /**
     * 查询全部服务组（env + appname）
     */
    public List<Instance> queryEnvAndAppName();

    /**
     * 分页查询Data，生效数据
     *
     * 生效逻辑：
     *      1、AUTO：心跳注册时间（register_heartbeat）非空，且在三倍心跳时间范围内；
     *      2、PERSISTENT：存在记录即可；
     *      3、DISABLE：忽略，不生效；
     */
    public List<Instance> queryByEnvAndAppNameValid(@Param("env") String env,
                                                    @Param("appname") String appname,
                                                    @Param("autoRegisterModel") int autoRegisterModel,
                                                    @Param("persistentRegisterModel") int persistentRegisterModel,
                                                    @Param("registerHeartbeatValid") Date registerHeartbeatValid);


    /**
     * 删除自动注册的实例
     *
     * 删除逻辑：
     *      1、仅 AUTO 注册类型节点允许删除；
     *
     * @param instance
     * @return
     */
    int deleteAutoInstance(@Param("instance") Instance instance);

    /**
     * 删除过期的实例
     *
     * 删除逻辑：
     *      1、仅 AUTO 注册类型节点允许删除；
     *      2、删除时间间隔超过 value 的实例；
     *
     * @param registerModel
     * @param registerHeartbeat
     * @return
     */
    int deleteExpiredAutoInstance(@Param("registerModel") int registerModel, @Param("registerHeartbeat") Date registerHeartbeat);

    /**
     * 新增自动注册的实例
     *
     * 新增逻辑：
     *      1、联合主键：env + appname + ip + port；如果已存在，则更新注册时间；
     *      2、若不存在 联合主键 数据，则新增 AUTO 类型节点；若存在 联合主键 数据，说明存在其他类型节点，只更新注册时间；
     *      3、返回值：// 0-fail; 1-save suc; 2-update suc;
     *
     * @param instance
     * @return
     */
    int addAutoInstance(@Param("instance") Instance instance);

    /**
     * 统计env下注册的实例数
     */
    int countByEnv(@Param("env") String env);

    /**
     * 统计所有实例数
     */
    int count();

}
