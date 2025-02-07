package com.xxl.conf.core.factory.support;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.annotation.XxlConf;
import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.factory.XxlConfFactory;
import com.xxl.conf.core.util.FieldReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * XxlConf Factory
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class SpringXxlConfFactory implements InitializingBean, DisposableBean, ApplicationContextAware, SmartInstantiationAwareBeanPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(SpringXxlConfFactory.class);


	// ---------------------- config ----------------------

	private String appname;

	private String env;

	private String address;

	private String accesstoken;

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}


	// ---------------------- start/stop ----------------------

	private XxlConfFactory xxlConfFactory;

	@Override
	public void afterPropertiesSet() throws Exception {
		// start
		xxlConfFactory = new XxlConfFactory(appname, env, address, accesstoken);
		xxlConfFactory.start();

		// add bean-refresh listner, for all key
		xxlConfFactory.getListenerRepository().addNoKeyListener(new SpringBeanRefreshListener());
	}

	@Override
	public void destroy() {
		xxlConfFactory.stop();
	}

	// ---------------------- post process / annotation ----------------------

	@Override
	public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

		// Annotation('@XxlConf')：resolves conf + watch
		if (!beanName.equals(SpringXxlConfFactory.class.getName())) {

			ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException {
					if (field.isAnnotationPresent(XxlConf.class)) {
						// original data
						String fieldName = field.getName();
						XxlConf xxlConf = field.getAnnotation(XxlConf.class);

						// parse data
						String appname = (xxlConf.appname()!=null&& !xxlConf.appname().trim().isEmpty())
								?xxlConf.appname()
								:xxlConfFactory.getAppname();
						String confKey = xxlConf.value();
						String confValue = XxlConfHelper.get(confKey, xxlConf.defaultValue());

						// resolves placeholders
						SpringBeanRefreshListener.BeanField beanField = new SpringBeanRefreshListener.BeanField(beanName, fieldName);
						refreshBeanField(bean, beanField, confValue);

						// watch
						if (xxlConf.callback()) {
							SpringBeanRefreshListener.addBeanField(appname, confKey, beanField);
						}

					}
				}
			});
		}

		return true;
	}

	// ---------------------- refresh bean with xxl conf  ----------------------

	/**
	 * refresh bean with xxl conf (fieldNames)
	 */
	public static void refreshBeanField(Object bean, final SpringBeanRefreshListener.BeanField beanField, final String value){

		// bean
		if (bean == null) {
			bean = applicationContext.getBean(beanField.getBeanName());
		}

		// reflect
		Field fieldItem;
		if (AopUtils.isAopProxy(bean)) {
			fieldItem = ReflectionUtils.findField(AopUtils.getTargetClass(bean), beanField.getFieldName());
		} else {
			fieldItem = ReflectionUtils.findField(bean.getClass(), beanField.getFieldName());
		}

		if (fieldItem != null) {
			try {
				// reflect invoke
				Object valueObj = FieldReflectionUtil.parseValue(fieldItem.getType(), value);

				//ReflectionUtils.makeAccessible(fieldItem);
				fieldItem.setAccessible(true);
				fieldItem.set(bean, valueObj);								// support mult data types

				logger.info(">>>>>>>>>>> xxl-conf, SpringXxlConfFactory refreshBeanField[reflect] success, {}#{}:{}", beanField.getBeanName(), beanField.getFieldName(), value);
			} catch (IllegalAccessException e) {
				throw new XxlConfException(e);
			}
		}

		/*// field, property descriptor
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(beanField.getFieldName());

		// refresh field
		if (propertyDescriptor!=null && propertyDescriptor.getWriteMethod() != null) {
			// set invoke
			Object valueObj = FieldReflectionUtil.parseValue(propertyDescriptor.getPropertyType(), value);
			beanWrapper.setPropertyValue(beanField.getFieldName(), valueObj);	// support mult data types
			logger.info(">>>>>>>>>>> xxl-conf, SpringXxlConfFactory refreshBeanField[set] success, {}#{}:{}", beanField.getBeanName(), beanField.getFieldName(), value);
		} else {
			// reflect
		}*/

	}

	// ---------------------- applicationContext ----------------------
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
