//package com.xxl.conf.core.core;
//
//import com.xxl.conf.core.exception.XxlConfException;
//import com.xxl.conf.core.util.XxlZkClient;
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
//public class XxlConfZkManageConf {
//	private static Logger logger = LoggerFactory.getLogger(XxlConfZkManageConf.class);
//
//
//	// ------------------------------ zookeeper client ------------------------------
//
//	private static final String zkBasePath = "/xxl-conf";
//	private static String getZkEnvPath(String env){
//		return zkBasePath.concat("/").concat(env);
//	}
//
//	private static XxlZkClient xxlZkClient = null;
//	public static void init(String zkaddress, String zkdigest) {
//
//		// valid
//		if (zkaddress==null || zkaddress.trim().length()==0) {
//			throw new XxlConfException("xxl-conf zkaddress can not be empty");
//		}
//
//		// init
//		xxlZkClient = new XxlZkClient(zkaddress, zkBasePath, zkdigest, new Watcher() {
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
//                        logger.info(">>>>>>>>>> xxl-conf, zk re-connect reloadAll success.");
//                    }
//
//                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        });
//
//		// init client
//		xxlZkClient.getClient();
//
//		logger.info(">>>>>>>>>> xxl-conf, XxlConfZkConf init success.");
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
//	public static void set(String env, String key, String data) {
//		String path = keyToPath(env, key);
//		xxlZkClient.setPathData(path, data, false);
//	}
//
//	/**
//	 * delete zk conf
//	 *
//	 * @param key
//	 */
//	public static void delete(String env, String key){
//		String path = keyToPath(env, key);
//		xxlZkClient.deletePath(path, false);
//	}
//
//	/**
//	 * get zk conf
//	 *
//	 * @param key
//	 * @return
//	 */
//	public static String get(String env, String key){
//		String path = keyToPath(env, key);
//		return xxlZkClient.getPathData(path, false);
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
//	public static String pathToKey(String env, String nodePath){
//		String zkEnvPath = getZkEnvPath(env);
//
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
//	public static String keyToPath(String env, String nodeKey){
//		String zkEnvPath = getZkEnvPath(env);
//
//		return zkEnvPath + "/" + nodeKey;
//	}
//
//}