//package com.xxl.conf.core.spring;
//
//import com.xxl.conf.core.XxlConfClient;
//import com.xxl.conf.core.annotation.XxlConf;
//import com.xxl.conf.core.core.XxlConfLocalCacheConf;
//import com.xxl.conf.core.core.XxlConfZkConf;
//import com.xxl.conf.core.exception.XxlConfException;
//import com.xxl.conf.core.listener.XxlConfListenerFactory;
//import com.xxl.conf.core.listener.impl.BeanRefreshXxlConfListener;
//import com.xxl.conf.core.util.PropUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.*;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.config.TypedStringValue;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.Ordered;
//import org.springframework.core.env.ConfigurablePropertyResolver;
//import org.springframework.util.ReflectionUtils;
//
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Field;
//import java.util.Properties;
//
///**
// * rewrite PropertyPlaceholderConfigurer
// *
// * @author xuxueli 2015-9-12 19:42:49
// */
//public class XxlConfFactory extends PropertySourcesPlaceholderConfigurer implements InitializingBean, DisposableBean, BeanPostProcessor {
//	private static Logger logger = LoggerFactory.getLogger(XxlConfFactory.class);
//
//	// ---------------------- env config ----------------------
//
//	private String envprop;
//	private String zkaddress;
//	private String zkpath;
//	private String zkdigest;
//
//	public void setEnvprop(String envprop) {
//		this.envprop = envprop;
//	}
//
//	public void setZkaddress(String zkaddress) {
//		this.zkaddress = zkaddress;
//	}
//
//	public void setZkpath(String zkpath) {
//		this.zkpath = zkpath;
//	}
//
//    public void setZkdigest(String zkdigest) {
//        this.zkdigest = zkdigest;
//    }
//
//    // ---------------------- init/destroy ----------------------
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//
//		// env prop
//		if (envprop!=null && envprop.trim().length()>0) {
//			Properties envPropFile = PropUtil.loadProp(envprop);
//			if (envPropFile!=null && envPropFile.stringPropertyNames()!=null && envPropFile.stringPropertyNames().size()>0) {
//				for (String key: envPropFile.stringPropertyNames()) {
//					if ("xxl.conf.zkaddress".equals(key)) {
//						zkaddress = envPropFile.getProperty(key);	// replace if envprop not exist
//					} else if ("xxl.conf.zkpath".equals(key)) {
//						zkpath = envPropFile.getProperty(key);
//					} else if ("xxl.conf.zkdigest".equals(key)) {
//						zkdigest = envPropFile.getProperty(key);
//                    }
//				}
//			}
//		}
//
//		// init
//		XxlConfZkConf.init(zkaddress, zkpath, zkdigest);									// init zk client
//        XxlConfLocalCacheConf.init();
//		XxlConfListenerFactory.addListener(null, new BeanRefreshXxlConfListener());    // listener all key change
//	}
//
//	@Override
//	public void destroy() throws Exception {
//		XxlConfLocalCacheConf.destroy();	// destroy ehcache
//		XxlConfZkConf.destroy();			// destroy zk client
//	}
//
//	// ---------------------- post process ----------------------
//
//	@Override
//	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//		return bean;
//	}
//
//	@Override
//	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//		return bean;
//	}
//
//	// ---------------------- spring xml/annotation conf ----------------------
//
//	private static final String placeholderPrefix = "$XxlConf{";
//	private static final String placeholderSuffix = "}";
//	private static boolean xmlKeyValid(String originKey){
//		boolean start = originKey.startsWith(placeholderPrefix);
//		boolean end = originKey.endsWith(placeholderSuffix);
//		if (start && end) {
//			return true;
//		}
//		return false;
//	}
//	private static String xmlKeyParse(String originKey){
//		if (xmlKeyValid(originKey)) {
//			// replace by xxl-conf
//			String key = originKey.substring(placeholderPrefix.length(), originKey.length() - placeholderSuffix.length());
//			return key;
//		}
//		return null;
//	}
//
//	/**
//	 * refresh bean with xxl conf (fieldNames)
//	 */
//	public static void refreshBeanField(BeanRefreshXxlConfListener.BeanField beanField, String value){
//		Object bean = beanFactory.getBean(beanField.getBeanName());		// TODO，springboot环境下，通过该方法 "getBean" 获取获取部分Bean，如Spring和Jackson等组件的Bean 会报错。原因未知；
//		if (bean != null) {
//			BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
//
//			// property descriptor
//			PropertyDescriptor propertyDescriptor = null;
//			PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
//			if (propertyDescriptors!=null && propertyDescriptors.length>0) {
//				for (PropertyDescriptor item: propertyDescriptors) {
//					if (beanField.getProperty().equals(item.getName())) {
//						propertyDescriptor = item;
//					}
//				}
//			}
//
//			// refresh field: set or field
//			if (propertyDescriptor!=null && propertyDescriptor.getWriteMethod() != null) {
//				beanWrapper.setPropertyValue(beanField.getProperty(), value);
//				logger.info(">>>>>>>>>>> xxl-conf, refreshBeanField[set] success, {}#{}:{}",
//						beanField.getBeanName(), beanField.getProperty(), value);
//			} else {
//				Field[] beanFields = bean.getClass().getDeclaredFields();
//				if (beanFields!=null && beanFields.length>0) {
//					for (Field fieldItem: beanFields) {
//						if (beanField.getProperty().equals(fieldItem.getName())) {
//							fieldItem.setAccessible(true);
//							try {
//								fieldItem.set(bean, value);
//								logger.info(">>>>>>>>>>> xxl-conf, refreshBeanField[field] success, {}#{}:{}",
//										beanField.getBeanName(), beanField.getProperty(), value);
//							} catch (IllegalAccessException e) {
//								throw new XxlConfException(e);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//
//	@Override
//	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
//		super.processProperties(beanFactoryToProcess, propertyResolver);
//
//		// BeanDefinitionVisitor
//		/*BeanDefinitionVisitor beanDefinitionVisitor = new BeanDefinitionVisitor(new StringValueResolver() {
//			@Override
//			public String resolveStringValue(String strVal) {
//				if (xmlKeyValid(strVal)) {
//					// object + property
//					String confKey = xmlKeyParse(strVal);
//					String confValue = XxlConfClient.get(confKey, "");
//
//					return confValue;
//				}
//				return strVal;
//			}
//		});
//		beanDefinitionVisitor.visitBeanDefinition(beanDefinition);*/
//
//		// visit bean definition
//		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
//		if (beanNames != null && beanNames.length > 0) {
//			for (final String beanName : beanNames) {
//				if (!(beanName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
//
//					// 1、XML('$XxlConf{...}')：resolves placeholders + watch
//					BeanDefinition beanDefinition = beanFactoryToProcess.getBeanDefinition(beanName);
//
//                    MutablePropertyValues pvs = beanDefinition.getPropertyValues();
//                    PropertyValue[] pvArray = pvs.getPropertyValues();
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
//								pvs.add(pv.getName(), confValue);
//
//								// watch
//								BeanRefreshXxlConfListener.BeanField beanField = new BeanRefreshXxlConfListener.BeanField(beanName, propertyName);
//								BeanRefreshXxlConfListener.addBeanField(confKey, beanField);
//							}
//						}
//					}
//
//					// 2、Annotation('@XxlConf')：resolves conf + watch
//					if (beanDefinition.getBeanClassName() == null) {
//						continue;
//					}
//					Class beanClazz = null;
//					try {
//						beanClazz = Class.forName(beanDefinition.getBeanClassName());
//					} catch (ClassNotFoundException e) {
//						logger.error(">>>>>>>>>>> xxl-conf, annotation bean class invalid, error msg:{}", e.getMessage());
//					}
//					if (beanClazz == null) {
//						continue;
//					}
//					ReflectionUtils.doWithFields(beanClazz, new ReflectionUtils.FieldCallback() {
//						@Override
//						public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//							if (field.isAnnotationPresent(XxlConf.class)) {
//								String propertyName = field.getName();
//								XxlConf xxlConf = field.getAnnotation(XxlConf.class);
//
//								String confKey = xxlConf.value();
//								String confValue = XxlConfClient.get(confKey, xxlConf.defaultValue());
//
//
//								// resolves placeholders
//								BeanRefreshXxlConfListener.BeanField beanField = new BeanRefreshXxlConfListener.BeanField(beanName, propertyName);
//								refreshBeanField(beanField, confValue);
//
//								// watch
//								if (xxlConf.callback()) {
//									BeanRefreshXxlConfListener.addBeanField(confKey, beanField);
//								}
//
//								/*field.setAccessible(true);
//								field.set(beanWithXxlConf, confValue);*/
//							}
//						}
//					});
//				}
//			}
//		}
//
//		logger.info(">>>>>>>>>>> xxl-conf, XxlConfFactory process success");
//	}
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
//	@Override
//	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
//		super.setIgnoreUnresolvablePlaceholders(true);
//	}
//
//}
