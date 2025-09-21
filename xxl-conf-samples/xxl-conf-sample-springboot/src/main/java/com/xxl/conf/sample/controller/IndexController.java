package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.annotation.XxlConf;
import com.xxl.conf.core.listener.XxlConfListener;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * @author xuxueli 2018-02-04 01:27:30
 */
@Controller
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(value = "/")
    public void index(HttpServletResponse response) throws IOException {

        /**
         * 方式1: API方式：XxlConfHelper.get("key")
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

        // html
        String htmlContent = front_html_template
                .replace("{0}", paramByApi)
                .replace("{1}", paramByAnnotation)
                .replace("{2}", paramByListener);

        // response
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(htmlContent);
    }

    /**
     * 方式2: 注解方式：@XxlConf
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

    /**
     * 方式3: 监听器方式：XxlConfHelper.addListener(...)
     *
     * 		- 参考 "IndexController" 中 "XxlConfHelper.addListener(...)" 即可；
     * 		- 用法：配置变更监听示例：可开发Listener逻辑，监听配置变更事件；可据此实现动态刷新 线程池、JDBC链接池 等高级功能；
     * 		- 优点：
     * 			- 监听器方式，扩展性更强；
     * 		    - 支持多数据类型
     * 			- 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
     */
    private String paramByListener;
    @PostConstruct
    public void initListener(){

        XxlConfHelper.addListener("sample.key03", new XxlConfListener(){
            @Override
            public void onChange(String appname, String key, String value) throws Exception {
                paramByListener = value;
                logger.info("XxlConfListener 配置变更事件通知：key={}, value={}", key, value);
            }
        });
    }

    // front html template
    private static String front_html_template = """
        <div class="container">
            <h1>XXL-CONF 配置获取方式示例</h1>
            <table style='border-collapse: collapse; width: 80%; font-family: Arial, sans-serif; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden;'>
                <thead>
                    <tr style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;'>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>序号</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>方式</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>配置Key</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>配置值</th>
                    </tr>
                </thead>
                <tbody>
                    <tr style='background-color: #f8f9fa; border-bottom: 1px solid #e9ecef;'>
                        <td style='padding: 12px 15px; font-weight: 500;'>1</td>
                        <td style='padding: 12px 15px;'>API方式：XxlConfHelper.get("key")</td>
                        <td style='padding: 12px 15px; font-family: monospace;'>sample.key01</td>
                        <td style='padding: 12px 15px; font-weight: 500;'>{0}</td>
                    </tr>
                    <tr style='background-color: #ffffff; border-bottom: 1px solid #e9ecef;'>
                        <td style='padding: 12px 15px; font-weight: 500;'>2</td>
                        <td style='padding: 12px 15px;'>注解方式：@XxlConf</td>
                        <td style='padding: 12px 15px; font-family: monospace;'>sample.key02</td>
                        <td style='padding: 12px 15px; font-weight: 500;'>{1}</td>
                    </tr>
                    <tr style='background-color:#f8f9fa;'>
                        <td style='padding: 12px 15px; font-weight: 500;'>3</td>
                        <td style='padding: 12px 15px;'>监听器方式：XxlConfListener</td>
                        <td style='padding: 12px 15px; font-family: monospace;'>sample.key03</td>
                        <td style='padding: 12px 15px; font-weight: 500;'>{2}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        """;

}
