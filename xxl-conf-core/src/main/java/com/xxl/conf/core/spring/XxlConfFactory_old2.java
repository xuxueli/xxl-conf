//package com.xxl.conf.core.spring;
//
//import com.xxl.conf.core.XxlConfClient;
//import com.xxl.conf.core.annotation.XxlConf;
//import com.xxl.conf.core.exception.XxlConfException;
//import com.xxl.conf.core.factory.XxlConfBaseFactory;
//import com.xxl.conf.core.listener.impl.BeanRefreshXxlConfListener;
//import com.xxl.conf.core.util.FieldReflectionUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.*;
//import org.springframework.beans.factory.*;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.config.TypedStringValue;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.core.Ordered;
//import org.springframework.core.PriorityOrdered;
//import org.springframework.util.ReflectionUtils;
//
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Field;
//import java.util.Objects;
//
///**
// * XxlConf Factory
// *
// * @author xuxueli 2015-9-12 19:42:49
// */
//public class XxlConfFactory_old2 implements InitializingBean, DisposableBean, BeanDefinitionRegistryPostProcessor,
//		PriorityOrdered, BeanNameAware, BeanFactoryAware {
//
//	private static Logger logger = LoggerFactory.getLogger(XxlConfFactory_old2.class);
//
//
//	// ---------------------- env config ----------------------
//
//	private String envprop;
//	private String zkaddress;
//	private String zkdigest;
//	private String env;
//
//	public void setEnvprop(String envprop) {
//		this.envprop = envprop;
//	}
//
//	public void setZkaddress(String zkaddress) {
//		this.zkaddress = zkaddress;
//	}
//
//    public void setZkdigest(String zkdigest) {
//        this.zkdigest = zkdigest;
//    }
//
//	public void setEnv(String env) {
//		this.env = env;
//	}
//
//	// ---------------------- init/destroy ----------------------
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//
//		if (envprop!=null && envprop.trim().length()>0) {
//			XxlConfBaseFactory.init(envprop);
//		} else {
//			XxlConfBaseFactory.init(zkaddress, zkdigest, env);
//		}
//
//	}
//
//	@Override
//	public void destroy() throws Exception {
//		XxlConfBaseFactory.destroy();
//	}
//
//
//	// ---------------------- post process / xml、annotation ----------------------
//
//	@Override
//	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//
//		// 1、Annotation('@XxlConf')：resolves conf + watch
//		//registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class, null);
//		registerBeanDefinitionIfNotExists(registry,  XxlConfAnnotationProcessor.class, null);
//
//		// 2、XML('$XxlConf{...}')：resolves placeholders + watch
//		String[] beanNames = registry.getBeanDefinitionNames();
//		if (beanNames != null && beanNames.length > 0) {
//			for (final String beanName : beanNames) {
//				if (!(beanName.equals(this.beanName) && registry.equals(XxlConfFactory_old2.beanFactory))) {
//
//					BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
//
//					MutablePropertyValues pvs = beanDefinition.getPropertyValues();
//					PropertyValue[] pvArray = pvs.getPropertyValues();
//					for (PropertyValue pv : pvArray) {
//						if (pv.getValue() instanceof TypedStringValue) {
//							String propertyName = pv.getName();
//							String typeStringVal = ((TypedStringValue) pv.getValue()).getValue();
//							if (xmlKeyValid(typeStringVal)) {
//
//								// object + property
//								String confKey = xmlKeyParse(typeStringVal);
//								String confValue = XxlConfClient.get(confKey, "");
//
//								// resolves placeholders
//								pvs.add(pv.getName(), confValue);	// support mult data types
//
//								// watch
//								BeanRefreshXxlConfListener.BeanField beanField = new BeanRefreshXxlConfListener.BeanField(beanName, propertyName);
//								BeanRefreshXxlConfListener.addBeanField(confKey, beanField);
//							}
//						}
//					}
//
//				}
//			}
//		}
//
//		logger.info(">>>>>>>>>>> xxl-conf, XxlConfFactory process success");
//	}
//
//	@Override
//	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		// TODO
//	}
//
//	public static class XxlConfAnnotationProcessor implements BeanPostProcessor, PriorityOrdered {
//
//		@Override
//		public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
//
//			ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
//				@Override
//				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//					if (field.isAnnotationPresent(XxlConf.class)) {
//						String propertyName = field.getName();
//						XxlConf xxlConf = field.getAnnotation(XxlConf.class);
//
//						String confKey = xxlConf.value();
//						String confValue = XxlConfClient.get(confKey, xxlConf.defaultValue());
//
//
//						// resolves placeholders
//						BeanRefreshXxlConfListener.BeanField beanField = new BeanRefreshXxlConfListener.BeanField(beanName, propertyName);
//						refreshBeanField(beanField, confValue, bean);
//
//						// watch
//						if (xxlConf.callback()) {
//							BeanRefreshXxlConfListener.addBeanField(confKey, beanField);
//						}
//
//					}
//				}
//			});
//
//			return bean;
//		}
//
//		@Override
//		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//			return bean;
//		}
//
//		@Override
//		public int getOrder() {
//			return Ordered.LOWEST_PRECEDENCE;
//		}
//
//	}
//
//
//	// ---------------------- refresh bean with xxl conf  ----------------------
//
//	/**
//	 * refresh bean with xxl conf (fieldNames)
//	 */
//	public static void refreshBeanField(BeanRefreshXxlConfListener.BeanField beanField, String value, Object bean){
//		if (bean == null) {
//			bean = XxlConfFactory_old2.beanFactory.getBean(beanField.getBeanName());		// TODO，springboot环境下，通过该方法 "getBean" 获取获取部分Bean，如Spring和Jackson等组件的Bean 会报错。原因未知；
//		}
//		if (bean == null) {
//			return;
//		}
//
//		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
//
//		// property descriptor
//		PropertyDescriptor propertyDescriptor = null;
//		PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
//		if (propertyDescriptors!=null && propertyDescriptors.length>0) {
//			for (PropertyDescriptor item: propertyDescriptors) {
//				if (beanField.getProperty().equals(item.getName())) {
//					propertyDescriptor = item;
//				}
//			}
//		}
//
//		// refresh field: set or field
//		if (propertyDescriptor!=null && propertyDescriptor.getWriteMethod() != null) {
//			beanWrapper.setPropertyValue(beanField.getProperty(), value);	// support mult data types
//			logger.info(">>>>>>>>>>> xxl-conf, refreshBeanField[set] success, {}#{}:{}",
//					beanField.getBeanName(), beanField.getProperty(), value);
//		} else {
//			Field[] beanFields = bean.getClass().getDeclaredFields();
//			if (beanFields!=null && beanFields.length>0) {
//				for (Field fieldItem: beanFields) {
//					if (beanField.getProperty().equals(fieldItem.getName())) {
//						try {
//							Object valueObj = FieldReflectionUtil.parseValue(fieldItem, value);
//
//							fieldItem.setAccessible(true);
//							fieldItem.set(bean, valueObj);		// support mult data types
//
//							logger.info(">>>>>>>>>>> xxl-conf, refreshBeanField[field] success, {}#{}:{}",
//									beanField.getBeanName(), beanField.getProperty(), value);
//						} catch (IllegalAccessException e) {
//							throw new XxlConfException(e);
//						}
//					}
//				}
//			}
//		}
//
//	}
//
//
//	// ---------------------- util ----------------------
//
//	/**
//	 * register beanDefinition If Not Exists
//	 *
//	 * @param registry
//	 * @param beanClass
//	 * @param beanName
//	 * @return
//	 */
//	public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, Class<?> beanClass, String beanName) {
//
//		// default bean name
//		if (beanName == null) {
//			beanName = beanClass.getName();
//		}
//
//		if (registry.containsBeanDefinition(beanName)) {	// avoid beanName repeat
//			return false;
//		}
//
//		String[] beanNameArr = registry.getBeanDefinitionNames();
//		for (String beanNameItem : beanNameArr) {
//			BeanDefinition beanDefinition = registry.getBeanDefinition(beanNameItem);
//			if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {	// avoid className repeat
//				return false;
//			}
//		}
//
//		BeanDefinition annotationProcessor = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
//		registry.registerBeanDefinition(beanName, annotationProcessor);
//		return true;
//	}
//
//
//	private static final String placeholderPrefix = "$XxlConf{";
//	private static final String placeholderSuffix = "}";
//
//	/**
//	 * valid xml
//	 *
//	 * @param originKey
//	 * @return
//	 */
//	private static boolean xmlKeyValid(String originKey){
//		boolean start = originKey.startsWith(placeholderPrefix);
//		boolean end = originKey.endsWith(placeholderSuffix);
//		if (start && end) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * parse xml
//	 *
//	 * @param originKey
//	 * @return
//	 */
//	private static String xmlKeyParse(String originKey){
//		if (xmlKeyValid(originKey)) {
//			// replace by xxl-conf
//			String key = originKey.substring(placeholderPrefix.length(), originKey.length() - placeholderSuffix.length());
//			return key;
//		}
//		return null;
//	}
//
//
//	// ---------------------- other ----------------------
//
//	@Override
//	public int getOrder() {
//		return Ordered.LOWEST_PRECEDENCE;
//	}
//
//	private String beanName;
//	@Override
//	public void setBeanName(String name) {
//		this.beanName = name;
//	}
//
//	private static BeanFactory beanFactory;
//	@Override
//	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//		this.beanFactory = beanFactory;
//	}
//
//
//}
