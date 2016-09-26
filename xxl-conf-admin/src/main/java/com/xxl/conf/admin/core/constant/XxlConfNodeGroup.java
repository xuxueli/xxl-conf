package com.xxl.conf.admin.core.constant;

/**
 * 配置按照项目进行分组, 分组前缀
 * Created by xuxueli on 16/9/26.
 */
public enum XxlConfNodeGroup {

    project_waimai("外卖项目"),
    project_dianying("电影项目"),
    project_tuangou("团购项目");

    private final String title;
    XxlConfNodeGroup(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
