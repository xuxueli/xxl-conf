package com.xxl.conf.admin.core.model;

/**
 * @author xuxueli 2018-03-01
 */
public class XxlConfUser {

    private String username;
    private String password;
    private int permission;      // 权限：0-普通用户、1-管理员

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
