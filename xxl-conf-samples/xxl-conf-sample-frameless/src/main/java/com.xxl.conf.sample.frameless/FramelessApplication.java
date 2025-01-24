package com.xxl.conf.sample.frameless;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.factory.XxlConfFactory;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-11-10 20:05:33
 */
public class FramelessApplication {
    private static final Logger logger = LoggerFactory.getLogger(FramelessApplication.class);


    public static void main(String[] args) {
        try {
            // start
            start();

            // test function
            testFunction();

            while (true) {
                TimeUnit.MINUTES.sleep(20);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // stop
            stop();
        }
    }

    private static void testFunction(){
        /**
         * 方式1: API方式
         *
         * 		- 用法：代码中直接调用API即可，参考 “XxlConfHelper.get” 相关API方法；
         * 		- 优点：
         * 			- API编程，灵活方便；
         * 		    - 支持多数据类型
         * 			- 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
         * 		    - 底层存在配置LocalCache，且存在缓存击穿等防护，性能有保障；
         */
        String paramByApi = XxlConfHelper.get("sample.key01", null);
        logger.info("\n 1、API方式: default.key01=" + paramByApi);

        /**
         * 方式2: Listener / 监听器方式
         *
         * 		- 用法：配置变更监听示例：可开发Listener逻辑，监听配置变更事件；可据此实现动态刷新 线程池、JDBC链接池 等高级功能；
         * 		- 优点：
         * 			- 监听器方式，扩展性更强；
         * 		    - 支持多数据类型
         * 			- 配置从配置中心实时加载，且底层存在动态推动更新，实效性有保障；
         */
        XxlConfHelper.addListener("sample.key02", new XxlConfListener(){
            @Override
            public void onChange(String appname, String key, String value) throws Exception {
                logger.info("\n 2、XxlConfListener 配置变更事件通知：key={}, value={}", key, value);
            }
        });
    }


    // ---------------------- xxl-conf config ----------------------

    private static XxlConfFactory xxlConfFactory;
    /**
     * start
     */
    public static void start() {
        Properties prop = PropUtil.loadProp("xxl-conf.properties");

        xxlConfFactory = new XxlConfFactory(
                prop.getProperty("xxl.conf.client.appname"),
                prop.getProperty("xxl.conf.client.env"),
                prop.getProperty("xxl.conf.admin.address"),
                prop.getProperty("xxl.conf.admin.accesstoken")
        );
        xxlConfFactory.start();
    }

    /**
     * stop
     */
    public static void stop() {
        xxlConfFactory.stop();
    }

}
