package com.xxl.conf.admin.service.impl;

import com.xxl.conf.core.util.XxlZkClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
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


	@Value("${xxl.conf.admin.zkaddress}")
	private String zkaddress;

	@Value("${xxl.conf.admin.zkpath}")
	private String zkpath;


	// ------------------------------ zookeeper client ------------------------------
	private static XxlZkClient xxlZkClient = null;
	@Override
	public void afterPropertiesSet() throws Exception {
		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				logger.info(">>>>>>>>>> xxl-conf: XxlConfManager watcher:{}", watchedEvent);

				// session expire, close old and create new
				if (watchedEvent.getState() == Event.KeeperState.Expired) {
					xxlZkClient.destroy();
					xxlZkClient.getClient();
					logger.info(">>>>>>>>>> xxl-conf, XxlConfManager re-connect success.");
				}
			}
		};

		xxlZkClient = new XxlZkClient(zkaddress, watcher);
		logger.info(">>>>>>>>>> xxl-conf, XxlConfManager init success.");
	}
	@Override
	public void destroy() throws Exception {
		if (xxlZkClient!=null) {
			xxlZkClient.destroy();
		}
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
		String path = keyToPath(key);
		xxlZkClient.setPathData(path, data);
	}

	/**
	 * delete zk conf
	 *
	 * @param key
	 */
	public void delete(String key){
		String path = keyToPath(key);
		xxlZkClient.deletePath(path);
	}

	/**
	 * get zk conf
	 *
	 * @param key
	 * @return
	 */
	public String get(String key){
		String path = keyToPath(key);
		return xxlZkClient.getPathData(path);
	}


	// ------------------------------ key 2 path / genarate key ------------------------------

	/**
	 * path 2 key
	 * @param nodePath
	 * @return ZnodeKey
	 */
	public String pathToKey(String nodePath){
		if (nodePath==null || nodePath.length() <= zkpath.length() || !nodePath.startsWith(zkpath)) {
			return null;
		}
		return nodePath.substring(zkpath.length()+1, nodePath.length());
	}

	/**
	 * key 2 path
	 * @param nodeKey
	 * @return znodePath
	 */
	public String keyToPath(String nodeKey){
		return zkpath + "/" + nodeKey;
	}


}