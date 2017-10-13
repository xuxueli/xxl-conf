package com.xxl.conf.core.annotation;


import com.xxl.conf.core.XxlConfClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @Value 注解处理器
 * User: chenchen_839@126.com
 */
public class ValueAnnotationProcessor implements ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(ValueAnnotationProcessor.class);
    private ApplicationContext applicationContext;
    private static XxlConfClient xxlConfClient = new XxlConfClient();
    //防止二次加载
    private boolean isinit = false;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (isinit) return;

        logger.info("ValueAnnotationProcessor->handler start");
        long startTime = System.currentTimeMillis();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            final Object object = applicationContext.getBean(beanName);
            ReflectionUtils.doWithFields(object.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    if (field.isAnnotationPresent(XxlValue.class)) {
                        XxlValue value = field.getAnnotation(XxlValue.class);
                        field.setAccessible(true);
                        String remoteValue = xxlConfClient.get(value.key(),value.defaultValue());
                        field.set(object,remoteValue);
                        if (value.callback()) {
                            XxlConfClient.addCallBack(value.key(), field, object);
                        }
                    }

                }
            });
        }
        logger.info("ValueAnnotationProcessor->handler finished cost time={}",System.currentTimeMillis()-startTime);
        isinit = true;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
