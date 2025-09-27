package com.xxl.conf.sample.controller;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.annotation.XxlConf;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author xuxueli 2018-02-04 01:27:30
 */
@Controller
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(value = "/")
    public void index(HttpServletResponse response) throws IOException {
        String htmlContent = front_html_template;

        // 动态配置获取
        htmlContent = htmlContent
                .replace("{0}", getDynamicConfByApi("sample.key01"))
                .replace("{1}", paramByAnnotation)
                .replace("{2}", paramByListener);


        // 动态线程池
        if (dynamicThreadPool!= null){
            String dynamicThreadDesc = "" +
                    "<ul>【1、线程配置】" +
                    "<li>核心线程数：" + dynamicThreadPool.getCorePoolSize() + "</li>" +
                    "<li>最大线程数：" + dynamicThreadPool.getMaximumPoolSize() + "</li>" +
                    "<li>空闲线程存活时间：" + dynamicThreadPool.getKeepAliveTime(TimeUnit.SECONDS) + "（秒）" + "</li>" +
                    "</ul>" +
                    "<ul>【2、线程监控】："  +
                    "<li>活跃线程数量：" + dynamicThreadPool.getActiveCount() + "</li>" +
                    "<li>历史最大线程数量：" + dynamicThreadPool.getLargestPoolSize() + "</li>" +
                    "</ul>" +
                    "<ul>【3、队列监控】：" +
                    "<li>等待队列中的任务数量：" + dynamicThreadPool.getQueue().size() + "</li>" +
                    "<li>队列剩余容量：" + dynamicThreadPool.getQueue().remainingCapacity() + "</li>" +
                    "<li>已完成任务数量：" + dynamicThreadPool.getCompletedTaskCount() + "</li>" +
                    "<li>已执行+未执行任务总数：" + dynamicThreadPool.getTaskCount() + "</li>" +
                    "</ul>" +
                    "<ul>【4、状态相关】："  +
                    "<li>线程池是否已关闭：" + dynamicThreadPool.isShutdown() + "</li>" +
                    "<li>线程池是否已终止：" + dynamicThreadPool.isTerminated() + "</li>" +
                    "<li>线程池是否正在终止过程中：" + dynamicThreadPool.isTerminating() + "</li>" +
                    "</ul>";
            mockSubmitTask();

            htmlContent = htmlContent.replace("{DynamicThread}", dynamicThreadDesc);
        } else {
            htmlContent = htmlContent.replace("{DynamicThread}", "未初始化");
        }

        // response
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(htmlContent);
    }


    // ---------------------- 动态配置获取 ----------------------

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
    public String getDynamicConfByApi(String key) {
        return XxlConfHelper.get(key, null);
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


    // ---------------------- 配置扩展使用 ----------------------


    // 动态线程池： 配置 + 实例
    private ThreadPoolExecutor dynamicThreadPool;
    private ThreadPoolConfig dynamicThreadConfig;

    /**
     * 扩展用法1: 动态线程池
     *
     * 		- 参考 "IndexController" 中 "initDynamicThread" 线程即可；
     * 		- 用法：监听动态线程池配置，监听创建/刷新动态线程池实例；
     */
    @PostConstruct
    public void initDynamicThread(){
        XxlConfHelper.addListener("sample.dynamic.threadpool", new XxlConfListener(){
            @Override
            public void onChange(String appname, String key, String value) throws Exception {
                if (StringTool.isBlank(value) || value.equals(GsonTool.toJson(dynamicThreadConfig))) {
                    logger.info("动态线程池，配置未变化，变更忽略");
                    return;
                }

                // 监听 DynamicThread 配置，更新线程池
                synchronized (this) {
                    try {
                        // refresh config
                        ThreadPoolConfig newConfig = GsonTool.fromJson(value, ThreadPoolConfig.class);
                        if (newConfig==null || newConfig.getCorePoolSize()<=0 || newConfig.getMaximumPoolSize()<=0 || newConfig.getKeepAliveTime()<0 || newConfig.getQueueCapacity()<=0) {
                            logger.info("动态线程池，新配置格式非法，变更失败");
                            return;
                        }
                        dynamicThreadConfig = newConfig;

                        // refresh threadPool
                        ThreadPoolExecutor oldThreadPool = dynamicThreadPool;
                        dynamicThreadPool = new ThreadPoolExecutor(
                                newConfig.getCorePoolSize(),
                                newConfig.getMaximumPoolSize(),
                                newConfig.getKeepAliveTime(),
                                TimeUnit.SECONDS,
                                new ArrayBlockingQueue<>(newConfig.getQueueCapacity()),
                                new RejectedExecutionHandler() {
                                    @Override
                                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                        logger.info("动态线程池，容量已满，任务提交拒绝：" + r);
                                    }
                                }
                        );

                        // shutdown old threadPool
                        if (oldThreadPool != null && !oldThreadPool.isShutdown()) {
                            // 非阻塞，拒绝新任务提交，存量任务执行结束后销毁；
                            oldThreadPool.shutdown();
                        }
                        logger.info("动态线程池, 配置变更通知：key={}, value={}", key, value);
                    } catch (Exception e) {
                        logger.error("动态线程池，配置更新失败: key={}, value={}", key, value, e);
                    }
                }
            }
        });
    }

    // 动态线程池配置信息
    public static class ThreadPoolConfig {
        private int corePoolSize;               // 核心线程数，即使线程空闲也不会被回收
        private int maximumPoolSize;            // 线程池允许的最大线程数
        private int keepAliveTime;              // 空闲线程存活时间
        private int queueCapacity;              // 队列长度（队列容量）

        public ThreadPoolConfig(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueCapacity) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
            this.queueCapacity = queueCapacity;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

    }

    // 模拟随机提交任务
    private void mockSubmitTask(){
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(50, 100); i++) {
            dynamicThreadPool.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {throw new RuntimeException(e);}
            });
        }
    }

    public static void main(String[] args) {
        ThreadPoolConfig config = new ThreadPoolConfig(10, 100, 60, 1000);
        System.out.println(GsonTool.toJson(config));
    }

    // ---------------------- other ----------------------

    // front html template
    private static final String front_html_template = """
        <div class="container">
            <h2>XXL-CONF 配置使用示例</h2>
            <h4>1、动态配置获取：</h4>
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
            
            <h4>2、配置扩展使用：动态线程池</h4>
            <table style='border-collapse: collapse; width: 80%; font-family: Arial, sans-serif; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden;'>
                <thead>
                    <tr style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;'>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>序号</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>方式</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>配置Key</th>
                        <th style='text-align: left; padding: 12px 15px; font-weight: 600;'>配置信息</th>
                    </tr>
                </thead>
                <tbody>
                    <tr style='background-color: #f8f9fa; border-bottom: 1px solid #e9ecef;'>
                        <td style='padding: 12px 15px; font-weight: 500;'>1</td>
                        <td style='padding: 12px 15px;'>动态线程池</td>
                        <td style='padding: 12px 15px; font-family: monospace;'>sample.dynamic.threadpool</td>
                        <td style='padding: 12px 15px; '>{DynamicThread}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        """;


}
