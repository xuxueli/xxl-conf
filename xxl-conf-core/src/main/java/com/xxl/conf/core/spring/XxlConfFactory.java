package com.xxl.conf.core.spring;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.annotaion.XxlConf;
import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfPropConf;
import com.xxl.conf.core.core.XxlConfZkClient;
import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import com.xxl.conf.core.listener.impl.AnnoRefreshXxlConfListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Field;

/**
 * rewrite PropertyPlaceholderConfigurer
 *
 * @author xuxueli 2015-9-12 19:42:49
 */
public class XxlConfFactory extends PropertySourcesPlaceholderConfigurer {
	private static Logger logger = LoggerFactory.getLogger(XxlConfFactory.class);

	public void init(){
	}
	public void destroy(){
		XxlConfLocalCacheConf.destroy();
		XxlConfZkClient.destroy();
	}

	/**
	 * xxl conf BeanDefinitionVisitor
	 *
	 * @return
	 */
	private static BeanDefinitionVisitor getXxlConfBeanDefinitionVisitor(){
		// xxl conf StringValueResolver
		StringValueResolver xxlConfValueResolver = new StringValueResolver() {
			String placeholderPrefix = "${";
			String placeholderSuffix = "}";
			@Override
			public String resolveStringValue(String strVal) {
				StringBuffer buf = new StringBuffer(strVal);
				// loop replace by xxl-conf, if the value match '${***}'
				boolean start = strVal.startsWith(placeholderPrefix);
				boolean end = strVal.endsWith(placeholderSuffix);
				while (start && end) {
					// replace by xxl-conf
					String key = buf.substring(placeholderPrefix.length(), buf.length() - placeholderSuffix.length());
					String zkValue = XxlConfClient.get(key, "");
					buf = new StringBuffer(zkValue);
					logger.info(">>>>>>>>>>> xxl conf, resolved placeholder success, [{}={}]", key, zkValue);
					start = buf.toString().startsWith(placeholderPrefix);
					end = buf.toString().endsWith(placeholderSuffix);
				}
				return buf.toString();
			}
		};

		// xxl conf BeanDefinitionVisitor
		BeanDefinitionVisitor xxlConfVisitor = new BeanDefinitionVisitor(xxlConfValueResolver);
		return xxlConfVisitor;
	}

	/**
	 * refresh bean with xxl conf
	 *
	 * @param beanWithXxlConf
	 */
	public static void refreshBeanWithXxlConf(final Object beanWithXxlConf){
		ReflectionUtils.doWithFields(beanWithXxlConf.getClass(), new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (field.isAnnotationPresent(XxlConf.class)) {
					XxlConf xxlConf = field.getAnnotation(XxlConf.class);
					String confKey = xxlConf.value();
					String confValue = XxlConfClient.get(confKey, xxlConf.defaultValue());

					field.setAccessible(true);
					field.set(beanWithXxlConf, confValue);
					logger.info(">>>>>>>>>>> xxl conf, refreshBeanWithXxlConf success, [{}={}]", confKey, confValue);
					if (xxlConf.callback()) {
						AnnoRefreshXxlConfListener.addKeyObject(confKey, beanWithXxlConf);
					}
				}
			}
		});
	}

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
		//super.processProperties(beanFactoryToProcess, propertyResolver);

		// xxl conf BeanDefinitionVisitor
		BeanDefinitionVisitor xxlConfVisitor = getXxlConfBeanDefinitionVisitor();

		// visit bean definition
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		if (beanNames != null && beanNames.length > 0) {
			for (String beanName : beanNames) {
				if (!(beanName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {

					// XML：resolves '${...}' placeholders within bean definition property values
					BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanName);
					xxlConfVisitor.visitBeanDefinition(bd);

					// Annotation：resolves '@XxlConf' annotations within bean definition fields
					final Object beanWithXxlConf = beanFactoryToProcess.getBean(beanName);
					refreshBeanWithXxlConf(beanWithXxlConf);	// refresh bean with xxl conf
				}
			}
		}

		logger.info(">>>>>>>>>>> xxl conf, XxlConfFactory process success");
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private String beanName;
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	private BeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		super.setIgnoreUnresolvablePlaceholders(true);
	}

}
