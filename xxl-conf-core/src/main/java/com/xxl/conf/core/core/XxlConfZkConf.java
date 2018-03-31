package com.xxl.conf.core.core;

import com.xxl.conf.core.env.Environment;
import com.xxl.conf.core.util.XxlZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ZooKeeper cfg client (Watcher + some utils)
 *
 * @author xuxueli 2015年8月26日21:36:43
 */
public class XxlConfZkConf {
	private static Logger logger = LoggerFactory.getLogger(XxlConfZkConf.class);


	// ------------------------------ zookeeper client ------------------------------

	private static XxlZkClient xxlZkClient = null;
	private static void init() {

		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				try {
					logger.info(">>>>>>>>>> xxl-conf: watcher:{}", watchedEvent);

					// session expire, close old and create new
					if (watchedEvent.getState() == Event.KeeperState.Expired) {
						xxlZkClient.destroy();
						xxlZkClient.getClient();
						XxlConfLocalCacheConf.reloadAll();
						logger.info(">>>>>>>>>> xxl-conf, zk re-connect reloadAll success.");
					}

					String path = watchedEvent.getPath();
					String key = pathToKey(path);
					if (key != null) {
						// keep watch conf key：add One-time trigger
						xxlZkClient.getClient().exists(path, true);
						if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
							// conf deleted
						} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
							// conf updated
							String data = get(key);
							XxlConfLocalCacheConf.update(key, data);
						}
					}
				} catch (KeeperException e) {
					logger.error(e.getMessage(), e);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		};

		xxlZkClient = new XxlZkClient(Environment.ZK_ADDRESS, watcher);
		logger.info(">>>>>>>>>> xxl-conf, XxlConfZkConf init success.");
	}
	static {
		init();
	}

	public static void destroy(){
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
	public static void set(String key, String data) {
		String path = keyToPath(key);
		xxlZkClient.setPathData(path, data);
	}

	/**
	 * delete zk conf
	 *
	 * @param key
	 */
	public static void delete(String key){
		String path = keyToPath(key);
		xxlZkClient.deletePath(path);
	}

	/**
	 * get zk conf
	 *
	 * @param key
	 * @return
	 */
	public static String get(String key){
		String path = keyToPath(key);
		return xxlZkClient.getPathData(path);
	}


	// ------------------------------ key 2 path / genarate key ------------------------------

	/**
	 * path 2 key
	 * @param nodePath
	 * @return ZnodeKey
	 */
	public static String pathToKey(String nodePath){
		if (nodePath==null || nodePath.length() <= Environment.ZK_PATH.length() || !nodePath.startsWith(Environment.ZK_PATH)) {
			return null;
		}
		return nodePath.substring(Environment.ZK_PATH.length()+1, nodePath.length());
	}

	/**
	 * key 2 path
	 * @param nodeKey
	 * @return znodePath
	 */
	public static String keyToPath(String nodeKey){
		return Environment.ZK_PATH + "/" + nodeKey;
	}

}