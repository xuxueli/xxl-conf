package com.xxl.conf.example.core.constant;

import com.xxl.conf.core.annotation.XxlValue;

/**
 * 测试使用，可删除
 * User: chenchen_839@126.com
 */
public class ConfigConstant {
    @XxlValue(key = "default.appname")
    private String value;

    public String getValue() {
        return value;
    }
}
