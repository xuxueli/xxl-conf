//package com.xxl.conf.admin.service.impl;
//
//import com.xxl.conf.core.core.XxlConfZkManageConf;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//
///**
// * ZooKeeper cfg client (Watcher + some utils)
// *
// * @author xuxueli 2015年8月26日21:36:43
// */
//@Component
//public class XxlConfZKManager implements InitializingBean, DisposableBean {
//	private static Logger logger = LoggerFactory.getLogger(XxlConfZKManager.class);
//
//	@Value("${xxl.conf.zkaddress:''}")
//	private String zkaddress;
//
//	@Value("${xxl.conf.zkdigest:''}")
//	private String zkdigest;
//
//	private static boolean open = false;
//	public static boolean isOpen() {
//		return open;
//	}
//
//	// ------------------------------ zookeeper client ------------------------------
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		if (zkaddress==null || zkaddress.trim().length()==0) {
//			open = false;
//			return;
//		}
//		XxlConfZkManageConf.init(zkaddress, zkdigest);
//	}
//
//	@Override
//	public void destroy() throws Exception {
//		if (!open) {
//			return;
//		}
//
//		XxlConfZkManageConf.destroy();
//	}
//
//
//	// ------------------------------ conf opt ------------------------------
//
//	/**
//	 * set zk conf
//	 *
//	 * @param key
//	 * @param data
//	 * @return
//	 */
//	public void set(String env, String key, String data) {
//		if (!open) {
//			return;
//		}
//
//		XxlConfZkManageConf.set(env, key, data);
//	}
//
//	/**
//	 * delete zk conf
//	 *
//	 * @param env
//	 * @param key
//	 */
//	public void delete(String env, String key){
//		if (!open) {
//			return;
//		}
//
//		XxlConfZkManageConf.delete(env, key);
//	}
//
//	/**
//	 * get zk conf
//	 *
//	 * @param key
//	 * @return
//	 */
//	public String get(String env, String key){
//		if (!open) {
//			return null;
//		}
//
//		return XxlConfZkManageConf.get(env, key);
//	}
//
//}