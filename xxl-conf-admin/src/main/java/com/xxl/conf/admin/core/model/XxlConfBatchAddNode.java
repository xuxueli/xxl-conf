package com.xxl.conf.admin.core.model;


import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 批量添加配置信息实体
 *
 * @author sunfeilong   (sunfl@cloud-young.com)
 * @version V1.0
 * @date 2018/9/5 14:41
 */
public class XxlConfBatchAddNode {

    private String env;
    private String appname;
    private String configText;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getConfigText() {
        return configText;
    }

    public void setConfigText(String configText) {
        this.configText = configText;
    }


    public List<XxlConfNode> translateToNodeList() {
        if ((!StringUtils.isEmpty(env)) && (!StringUtils.isEmpty(appname)) && (!StringUtils.isEmpty(configText))) {
            String[] keyValueLines = configText.split("\r\n");
            if (keyValueLines.length > 0) {
                List<XxlConfNode> result = new ArrayList<>();
                for (String keyValueLine : keyValueLines) {
                    if (keyValueLine.startsWith("#") || keyValueLine.startsWith("=")) {
                        continue;
                    }
                    int splitCharIndex = keyValueLine.indexOf('=');
                    if (splitCharIndex == -1) {
                        continue;
                    }
                    String key = keyValueLine.substring(0, splitCharIndex);
                    String value = "";
                    if (splitCharIndex + 1 <= keyValueLine.length()) {
                        value = keyValueLine.substring(splitCharIndex + 1);
                    }
                    XxlConfNode xxlConfNode = new XxlConfNode();
                    xxlConfNode.setEnv(env);
                    xxlConfNode.setAppname(appname);
                    xxlConfNode.setKey((appname + "." + key).trim());
                    xxlConfNode.setValue(value);
                    xxlConfNode.setTitle("");
                    result.add(xxlConfNode);
                }
                return result;
            }
        }
        return Collections.emptyList();
    }
}
