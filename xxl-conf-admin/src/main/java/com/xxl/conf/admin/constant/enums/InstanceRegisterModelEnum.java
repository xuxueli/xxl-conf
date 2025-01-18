package com.xxl.conf.admin.constant.enums;

/**
 * Instance Model Enum
 *
 * @author xuxueli
 */
public enum InstanceRegisterModelEnum {

    /**
     * 动态注册
     *
     * 1、生效判定逻辑：心跳注册时间（register_heartbeat）非空，且在三倍心跳时间范围内；
     * 2、注册：心跳注册时间 更新为当前时间；
     * 3、注销：心跳注册时间 置为空值；
     */
    AUTO(0, "动态注册"),

    /**
     * 持久化注册
     *
     * 1、生效判定逻辑：永久生效
     * 2、注册：添加记录
     * 3、注销：删除记录
     */
    PERSISTENT(1, "持久化注册"),

    /**
     * 禁用注册
     *
     * 1、生效判定逻辑：无；存在记录说明注册无效，且该地址不允许注册；
     * 2、注册：禁止注册
     * 3、注销：不需要，该地址不允许注册；
     */
    DISABLE(2, "禁用注册");

    private int value;
    private String desc;

    InstanceRegisterModelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * match by val
     *
     * @param value
     * @return
     */
    public static InstanceRegisterModelEnum match(int value) {
        for (InstanceRegisterModelEnum instanceModelEnum : InstanceRegisterModelEnum.values()) {
            if (instanceModelEnum.getValue() == value) {
                return instanceModelEnum;
            }
        }
        return null;
    }
}
