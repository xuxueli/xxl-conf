package com.xxl.conf.admin.constant.enums;

import com.xxl.conf.admin.constant.consts.Consts;

import java.util.*;

/**
 * role enum
 *
 * @author xuxueli
 */
public enum RoleEnum {

    ADMIN(Consts.ADMIN_ROLE, "管理员", new ArrayList<>()),
    NORMAL("NORMAL", "普通用户", new ArrayList<>());

    private String value;
    private String desc;
    private List<String> permissions;

    RoleEnum(String value, String desc, List<String> permissions) {
        this.value = value;
        this.desc = desc;
        this.permissions = permissions;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    // tool
    /**
     * load by value
     *
     * @param value
     * @return
     */
    public static RoleEnum matchByValue(String value) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }

}
