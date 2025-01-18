package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.annotation.XxlConf;
import com.xxl.conf.core.listener.XxlConfListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xuxueli 2018-02-04 01:27:30
 */
@Controller
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * 方式1: @XxlConf 注解方式
     *
     * 		- 参考 "IndexController.paramByAnnotation" 属性配置；示例代码 "@XxlConf("key") public String paramByAnnotation;"；
     * 		- 用法：对象Field上加注解 ""@XxlConf"；支持设置默认值、跨服务复用配置，以及设置是否动态刷新；
     * 		- 优点：
     * 			- 注解编程，简洁易用；
     * 		    - 支持多数据类型
     * 		    - 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
     * 		    - 注解属性自身承担数据存储职责，无外部请求逻辑，无性能风险；
     */
    @XxlConf("sample.key02")
    public String paramByAnnotation;

    @RequestMapping("")
    @ResponseBody
    public String index(){

        String result = "";

        /**
         * 方式2: API方式
         *
         * 		- 参考 "IndexController" 中 "XxlConfHelper.get("key")" 即可；
         * 		- 用法：代码中直接调用API即可，API支持多数据类型，可快速获取各类型配置；
         * 		- 优点：
         * 			- API编程，灵活方便；
         * 		    - 支持多数据类型
         * 			- 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
         * 		    - 底层存在配置LocalCache，且存在缓存击穿等防护，性能有保障；
         */
        String paramByApi = XxlConfHelper.get("sample.key01", null);

        // response
        result += ("<br> 1、API方式（XxlConfHelper）: sample.key01=" + paramByApi);
        result += ("<br> 2、注解方式（@XxlConf）: sample.key02=" + paramByAnnotation);
        result += ("<br> 3、监听器方式（XxlConfListener）: sample.key03=" + paramByListener);
        return result;
    }

    private static String paramByListener;
    @PostConstruct
    public void initListener(){
        /**
         * 方式3: Listener / 监听器方式
         *
         * 		- 参考 "IndexController" 中 "XxlConfHelper.addListener(...)" 即可；
         * 		- 用法：配置变更监听示例：可开发Listener逻辑，监听配置变更事件；可据此实现动态刷新 线程池、JDBC链接池 等高级功能；
         * 		- 优点：
         * 			- 监听器方式，扩展性更强；
         * 		    - 支持多数据类型
         * 			- 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
         */
        XxlConfHelper.addListener("sample.key03", new XxlConfListener(){
            @Override
            public void onChange(String appname, String key, String value) throws Exception {
                paramByListener = value;
                logger.info("XxlConfListener 配置变更事件通知：key={}, value={}", key, value);
            }
        });
    }

}
