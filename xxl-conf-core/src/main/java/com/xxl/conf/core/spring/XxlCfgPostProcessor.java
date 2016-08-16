package com.xxl.conf.core.spring;

import com.xxl.conf.core.XxlCfgLocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.StringValueResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * invoke between prop load and bean init	PlaceholderConfigurerSupport
 * @version 2.0
 * @author xuxueli 2015-9-24 20:20:43	
 * 	
 * 	<bean id="xxlCfgPostProcessor" class="com.xxl.cfg.spring.XxlCfgPostProcessor">
        <property name="localPropPath" value="local.properties" />
    </bean>
 */
public class XxlCfgPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered, BeanNameAware, BeanFactoryAware{
	private static Logger logger = LoggerFactory.getLogger(XxlCfgPostProcessor.class);
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// set local prop
		initLocalProp();
		
		// init value resolver
		StringValueResolver valueResolver = new StringValueResolver() {
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
		        	String zkValue = XxlCfgLocalCache.get(key, "");
	        		buf = new StringBuffer(zkValue);
	        		logger.info(">>>>>>>>>>> xxl-conf resolved placeholder '" + key + "' to value [" + zkValue + "]");
		        	start = buf.toString().startsWith(placeholderPrefix);
		        	end = buf.toString().endsWith(placeholderSuffix);
		        }
		        return buf.toString();
			}
		};
		
		// init bean define visitor
		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
		
		// visit bean definition
		String[] beanNames = beanFactory.getBeanDefinitionNames();
		if (beanNames != null && beanNames.length > 0) {
			for (String beanName : beanNames) {
				if (!(beanName.equals(this.beanName) && beanFactory.equals(this.beanFactory))) {
					BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
					visitor.visitBeanDefinition(bd);
				}
			}
		}
		
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
	
	// local prop path
	private String localPropPath;
	/**
	 * init local prop
	 */
	private void initLocalProp(){
		// set local prop
		if (localPropPath != null && localPropPath.trim().length() > 0) {
			Properties localProp;
			InputStreamReader in = null;
			try {
				ClassLoader loder = Thread.currentThread() .getContextClassLoader();
				URL url = loder.getResource(localPropPath);
				in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
				if (in != null) {
					localProp = new Properties();
					localProp.load(in);
					XxlCfgLocalCache.setLocalProp(localProp);
				}
			} catch (Exception e) {
				logger.error(">>>>>>>>> xxl-conf local prop file not exists, file name : {}", localPropPath);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						logger.error(">>>>>>>>>close {} error!", localPropPath);
					}
				}
			}
		}
	}

	public String getLocalPropPath() {
		return localPropPath;
	}
	public void setLocalPropPath(String localPropPath) {
		this.localPropPath = localPropPath;
	}

}
