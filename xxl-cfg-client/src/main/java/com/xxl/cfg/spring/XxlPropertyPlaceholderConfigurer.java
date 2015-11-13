package com.xxl.cfg.spring;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.xxl.cfg.core.XxlCfgClient;

/**
 * rewrite PropertyPlaceholderConfigurer
 * @version 1.0
 * @author xuxueli 2015-9-12 19:42:49
 * 
 * 	<!-- xxl-cfg 
	<bean id="zkPropertyConfigurer" class="com.xxl.cfg.spring.XxlPropertyPlaceholderConfigurer">
		<property name="order" value="2"/>
		<property name="ignoreUnresolvablePlaceholders" value="true" />
		<property name="rootKey" value="xxl-cfg-demo" />
		<property name="locations">  
           <list>  
              <value>classpath:local.properties</value>  
            </list>  
        </property>  
	</bean>
	-->
 */
public class XxlPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private static Logger logger = LoggerFactory.getLogger(XxlPropertyPlaceholderConfigurer.class);
	
	// app key,
	private String rootKey;
	public String getRootKey() {
		return rootKey;
	}
	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}


	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		
		// automatically loaded all keys that like >> rootKey.key001 
		if (StringUtils.isNotBlank(rootKey)) {
			Map<String, String> allData = XxlCfgClient.client.getAllData();
			if (MapUtils.isNotEmpty(allData)) {
				for (Entry<String, String> item : allData.entrySet()) {
					// pull props belongs to rootKey, if not exists in local properties files
					if (props.get(item.getKey()) == null && item.getKey().startsWith(rootKey)) {
						props.put(item.getKey(), item.getValue());
						logger.info("load property from zk：{}={}", new Object[]{item.getKey(), item.getValue()});
					}
				}
			}
			
		}
		
		// TODO Auto-generated method stub
		super.processProperties(beanFactoryToProcess, props);
	}
}
