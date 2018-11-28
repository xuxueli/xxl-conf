//package com.xxl.conf.core.core;
//
//import com.xxl.conf.core.exception.XxlConfException;
//import com.xxl.conf.core.util.XxlZkClient;
//import org.apache.zookeeper.KeeperException;
//import org.apache.zookeeper.WatchedEvent;
//import org.apache.zookeeper.Watcher;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * ZooKeeper cfg client (Watcher + some utils)
// *
// * @author xuxueli 2015年8月26日21:36:43
// */
//public class XxlConfZkConf {
//	private static Logger logger = LoggerFactory.getLogger(XxlConfZkConf.class);
//
//
//	// ------------------------------ zookeeper client ------------------------------
//
//	private static final String zkBasePath = "/xxl-conf";
//	private static String zkEnvPath;
//
//	private static XxlZkClient xxlZkClient = null;
//	public static void init(String zkaddress, String zkdigest, String env) {
//
//		// valid
//		if (zkaddress==null || zkaddress.trim().length()==0) {
//			throw new XxlConfException("xxl-conf zkaddress can not be empty");
//		}
//
//		// init zkpath
//		if (env==null || env.trim().length()==0) {
//			throw new XxlConfException("xxl-conf env can not be empty");
//		}
//
//        XxlConfZkConf.zkEnvPath = zkBasePath.concat("/").concat(env);
//
//		// init
//		xxlZkClient = new XxlZkClient(zkaddress, zkEnvPath, zkdigest, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                try {
//                    logger.info(">>>>>>>>>> xxl-conf: watcher:{}", watchedEvent);
//
//                    // session expire, close old and create new
//                    if (watchedEvent.getState() == Event.KeeperState.Expired) {
//                        xxlZkClient.destroy();
//                        xxlZkClient.getClient();
//
//
//						XxlConfLocalCacheConf.reloadAll();
//                        logger.info(">>>>>>>>>> xxl-conf, zk re-connect reloadAll success.");
//                    }
//
//					String path = watchedEvent.getPath();
//					String key = pathToKey(path);
//					if (key != null) {
//						// keep watch conf key：add One-time trigger
//						xxlZkClient.getClient().exists(path, true);
//						if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
//							// conf deleted
//						} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
//							// conf updated
//							String data = get(key);
//							XxlConfLocalCacheConf.update(key, data);
//						}
//					}
//
//                } catch (KeeperException e) {
//                    logger.error(e.getMessage(), e);
//                } catch (InterruptedException e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        });
//
//		// init client
//		try {
//			xxlZkClient.getClient();
//			logger.info(">>>>>>>>>> xxl-conf, XxlConfZkConf init success. [env={}]", env);
//		} catch (Exception e) {
//			logger.info(">>>>>>>>>> xxl-conf, XxlConfZkConf init error, will retry. [env={}]", env);
//			logger.error(e.getMessage(), e);
//		}
//	}
//
//	public static void destroy(){
//		if (xxlZkClient!=null) {
//			xxlZkClient.destroy();
//		}
//	}
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
//	public static void set(String key, String data) {
//		String path = keyToPath(key);
//		xxlZkClient.setPathData(path, data, false);
//	}
//
//	/**
//	 * delete zk conf
//	 *
//	 * @param key
//	 */
//	public static void delete(String key){
//		String path = keyToPath(key);
//		xxlZkClient.deletePath(path, false);
//	}
//
//	/**
//	 * get zk conf
//	 *
//	 * @param key
//	 * @return
//	 */
//	public static String get(String key){
//		String path = keyToPath(key);
//		return xxlZkClient.getPathData(path, true);
//	}
//
//
//	// ------------------------------ key 2 path / genarate key ------------------------------
//
//	/**
//	 * path 2 key
//	 * @param nodePath
//	 * @return ZnodeKey
//	 */
//	public static String pathToKey(String nodePath){
//		if (nodePath==null || nodePath.length() <= zkEnvPath.length() || !nodePath.startsWith(zkEnvPath)) {
//			return null;
//		}
//		return nodePath.substring(zkEnvPath.length()+1, nodePath.length());
//	}
//
//	/**
//	 * key 2 path
//	 * @param nodeKey
//	 * @return znodePath
//	 */
//	public static String keyToPath(String nodeKey){
//		return zkEnvPath + "/" + nodeKey;
//	}
//
//}