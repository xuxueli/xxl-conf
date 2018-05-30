package com.xxl.conf.admin.core.model;

/**
 * Created by xuxueli on 2018-05-30
 */
public class XxlConfEnv {

    private String env;         // Env
    private String title;       // 环境名称
    private int order;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
