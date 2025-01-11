package com.xxl.conf.admin.service;

import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.dto.UserDTO;
import com.xxl.conf.admin.model.entity.User;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import java.util.List;

/**
 * user service
 *
 * @author xuxueli
 */
public interface UserService {

    /**
     * 新增
     */
    public Response<String> insert(UserDTO xxlJobUser);

    /**
     * 删除
     */
    public Response<String> delete(int id);

    /**
     * 删除
     */
    Response<String> deleteByIds(List<Integer> userIds, LoginUserDTO loginUser);

    /**
     * 更新
     */
    public Response<String> update(UserDTO xxlJobUser, LoginUserDTO loginUser);

    /**
     * 修改密码
     */
    public Response<String> updatePwd(LoginUserDTO loginUser, String password);

    /**
     * Load查询
     */
    public Response<User> loadByUserName(String username);

    /**
     * 授权权限
     */
    public Response<String> grantPermission(String username, String permission);

    /**
     * 分页查询
     */
    public PageModel<UserDTO> pageList(int offset, int pagesize, String username, int status);

}
