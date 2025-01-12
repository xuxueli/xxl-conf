package com.xxl.conf.admin.constant.enums;

/**
 * message type
 *
 * @author xuxueli
 */
public enum MessageTypeEnum {

    /**
     * 服务注册，包含 服务注册，服务下线等；
     */
    REGISTRY(1, "服务注册变更"),
    CONFDATA(2, "配置数据变更");

    private int value;
    private String desc;

    MessageTypeEnum(int value, String desc) {
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

}
