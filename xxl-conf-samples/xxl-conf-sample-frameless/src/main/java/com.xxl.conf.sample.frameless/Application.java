package com.xxl.conf.sample.frameless;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.sample.frameless.conf.FrameLessXxlConfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-11-10 20:05:33
 */
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            // start
            FrameLessXxlConfConfig.init();

            // test listener test
            XxlConfClient.addListener("default.key01", new XxlConfListener(){
                @Override
                public void onChange(String key, String value) throws Exception {
                    logger.info("配置变更事件通知：{}={}", key, value);
                }
            });


            while (true) {
                TimeUnit.HOURS.sleep(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // destory
            FrameLessXxlConfConfig.destroy();
        }
    }

}
