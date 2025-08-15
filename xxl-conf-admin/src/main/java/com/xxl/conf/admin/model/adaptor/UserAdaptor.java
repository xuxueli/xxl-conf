package com.xxl.conf.admin.model.adaptor;

import com.xxl.conf.admin.model.dto.LoginUserDTO;
import com.xxl.conf.admin.model.dto.UserDTO;
import com.xxl.conf.admin.model.entity.User;

public class UserAdaptor {

    public static User adapt(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User xxlUser = new User();
        xxlUser.setId(userDTO.getId());
        xxlUser.setUsername(userDTO.getUsername());
        xxlUser.setPassword(userDTO.getPassword());
        xxlUser.setToken(userDTO.getToken());
        xxlUser.setStatus(userDTO.getStatus());
        xxlUser.setRealName(userDTO.getRealName());
        xxlUser.setRole(userDTO.getRole());
        xxlUser.setAddTime(userDTO.getAddTime());
        xxlUser.setUpdateTime(userDTO.getUpdateTime());
        return xxlUser;
    }

    public static UserDTO adapt(User user) {
        if (user == null) {
            return null;
        }

        UserDTO xxlUser = new UserDTO();
        xxlUser.setId(user.getId());
        xxlUser.setUsername(user.getUsername());
        xxlUser.setPassword(user.getPassword());
        xxlUser.setToken(user.getToken());
        xxlUser.setStatus(user.getStatus());
        xxlUser.setRealName(user.getRealName());
        xxlUser.setRole(user.getRole());
        xxlUser.setAppnames(user.getAppnames());
        xxlUser.setAddTime(user.getAddTime());
        xxlUser.setUpdateTime(user.getUpdateTime());
        return xxlUser;
    }

    /**
     * adapt to login user
     *
     * @param user
     * @return
     */
    public static LoginUserDTO adapt2LoginUser(User user) {
        if (user == null) {
            return null;
        }

        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setId(user.getId());
        loginUserDTO.setUsername(user.getUsername());
        loginUserDTO.setPassword(user.getPassword());
        loginUserDTO.setToken(user.getToken());
        loginUserDTO.setRealName(user.getRealName());
        loginUserDTO.setRole(user.getRole());
        loginUserDTO.setPermission(user.getAppnames());

        return loginUserDTO;
    }

}
