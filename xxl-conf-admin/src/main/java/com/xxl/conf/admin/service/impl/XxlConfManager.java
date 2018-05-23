package com.xxl.conf.admin.service.impl;

import com.xxl.conf.core.core.XxlConfZkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * ZooKeeper cfg client (Watcher + some utils)
 *
 * @author xuxueli 2015年8月26日21:36:43
 */
@Component
public class XxlConfManager implements InitializingBean, DisposableBean {
	private static Logger logger = LoggerFactory.getLogger(XxlConfManager.class);

	@Value("${xxl.conf.zkaddress}")
	private String zkaddress;

	@Value("${xxl.conf.zkdigest}")
	private String zkdigest;

	@Value("${xxl.conf.env}")
	private String env;

	// ------------------------------ zookeeper client ------------------------------

	@Override
	public void afterPropertiesSet() throws Exception {
		XxlConfZkConf.init(zkaddress, zkdigest, env, false);
	}

	@Override
	public void destroy() throws Exception {
		XxlConfZkConf.destroy();
	}


	// ------------------------------ conf opt ------------------------------

	/**
	 * set zk conf
	 *
	 * @param key
	 * @param data
	 * @return
	 */
	public void set(String key, String data) {
		XxlConfZkConf.set(key, data);
	}

	/**
	 * delete zk conf
	 *
	 * @param key
	 */
	public void delete(String key){
		XxlConfZkConf.delete(key);
	}

	/**
	 * get zk conf
	 *
	 * @param key
	 * @return
	 */
	public String get(String key){
		return XxlConfZkConf.get(key);
	}

}