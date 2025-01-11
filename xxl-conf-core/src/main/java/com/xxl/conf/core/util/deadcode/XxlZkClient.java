//package com.xxl.conf.core.util;
//
//import com.xxl.conf.core.exception.XxlConfException;
//import org.apache.zookeeper.*;
//import org.apache.zookeeper.data.Stat;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.ReentrantLock;
//
//
///**
// * ZooKeeper cfg client (Watcher + some utils)
// *
// * @author xuxueli 2015年8月26日21:36:43
// *
// *         Zookeeper
// *         从设计模式角度来看，是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理大家都关心的数据，然后接受观察者的注册
// *         ，一旦这些数据的状态发生变化，Zookeeper 就将负责通知已经在 Zookeeper
// *         上注册的那些观察者做出相应的反应，从而实现集群中类似 Master/Slave 管理模式
// *
// *         1、统一命名服务（Name Service）:将有层次的目录结构关联到一定资源上，广泛意义上的关联，也许你并不需要将名称关联到特定资源上，
// *         你可能只需要一个不会重复名称。
// *
// *         2、配置管理（Configuration Management）：分布式统一配置管理：将配置信息保存在
// *         Zookeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到
// *         Zookeeper 的通知，然后从 Zookeeper 获取新的配置信息应用到系统中
// *
// *         3、集群管理（Group
// *         Membership）:Zookeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，那么必须
// *         要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，集群中其它集群必须知道，从而做出调整重新分配服务策略。
// *         同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道。
// *
// *         4、共享锁（Locks）：
// *         5、队列管理：a、当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。b、队列按照 FIFO 方式
// *         进行入队和出队操作，例如实现生产者和消费者模型。
// *
// *         集中式配置管理 动态更新
// *
// *         特性：
// *         		- 断线重连Watch
// *         		- 重入锁
// *         		- 实用util
// */
//public class XxlZkClient {
//	private static Logger logger = LoggerFactory.getLogger(XxlZkClient.class);
//
//
//	private String zkaddress;
//	private String zkpath;
//	private String zkdigest;
//	private Watcher watcher;	// watcher(One-time trigger)
//
//
//	public XxlZkClient(String zkaddress, String zkpath, String zkdigest, Watcher watcher) {
//
//		this.zkaddress = zkaddress;
//		this.zkpath = zkpath;
//		this.zkdigest = zkdigest;
//		this.watcher = watcher;
//
//		// reconnect when expire
//		if (this.watcher == null) {
//			// watcher(One-time trigger)
//			this.watcher = new Watcher() {
//				@Override
//				public void process(WatchedEvent watchedEvent) {
//					logger.info(">>>>>>>>>> xxl-conf: watcher:{}", watchedEvent);
//
//					// session expire, close old and create new
//					if (watchedEvent.getState() == Event.KeeperState.Expired) {
//						destroy();
//						getClient();
//					}
//				}
//			};
//		}
//
//		//getClient();		// async coon, support init without conn
//	}
//
//	// ------------------------------ zookeeper client ------------------------------
//	private ZooKeeper zooKeeper;
//	private ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock(true);
//	public ZooKeeper getClient(){
//		if (zooKeeper==null) {
//			try {
//				if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
//
//					// init new-client
//					ZooKeeper newZk = null;
//					try {
//						if (zooKeeper==null) {		// 二次校验，防止并发创建client
//							newZk = new ZooKeeper(zkaddress, 10000, watcher);
//							if (zkdigest!=null && zkdigest.trim().length()>0) {
//								newZk.addAuthInfo("digest",zkdigest.getBytes());		// like "account:password"
//							}
//							newZk.exists(zkpath, false);		// sync wait until succcess conn
//
//							// set success new-client
//							zooKeeper = newZk;
//							logger.info(">>>>>>>>>> xxl-conf, XxlZkClient init success.");
//						}
//					} catch (Exception e) {
//						// close fail new-client
//						if (newZk != null) {
//							newZk.close();
//						}
//
//						logger.error(e.getMessage(), e);
//					} finally {
//						INSTANCE_INIT_LOCK.unlock();
//					}
//
//				}
//			} catch (InterruptedException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//		if (zooKeeper == null) {
//			throw new XxlConfException("XxlZkClient.zooKeeper is null.");
//		}
//		return zooKeeper;
//	}
//
//	public void destroy(){
//		if (zooKeeper!=null) {
//			try {
//				zooKeeper.close();
//				zooKeeper = null;
//			} catch (InterruptedException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//	}
//
//	// ------------------------------ util ------------------------------
//
//	/**
//	 * create node path with parent path (PERSISTENT)
//     * 	 *
//     * 	 * zk limit parent must exist
//	 *
//	 * @param path
//	 */
//	private Stat createPathWithParent(String path, boolean watch){
//		// valid
//		if (path==null || path.trim().length()==0) {
//			return null;
//		}
//
//		try {
//			Stat stat = getClient().exists(path, watch);
//			if (stat == null) {
//				//  valid parent, createWithParent if not exists
//				if (path.lastIndexOf("/") > 0) {
//					String parentPath = path.substring(0, path.lastIndexOf("/"));
//					Stat parentStat = getClient().exists(parentPath, watch);
//					if (parentStat == null) {
//						createPathWithParent(parentPath, false);
//					}
//				}
//				// create desc node path
//				getClient().create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//			}
//			return getClient().exists(path, true);
//		} catch (Exception e) {
//			throw new XxlConfException(e);
//		}
//	}
//
//	/**
//	 * delete path (watch)
//	 *
//	 * @param path
//	 */
//	public void deletePath(String path, boolean watch){
//		try {
//			Stat stat = getClient().exists(path, watch);
//			if (stat != null) {
//				getClient().delete(path, stat.getVersion());
//			} else {
//				logger.info(">>>>>>>>>> zookeeper node path not found :{}", path);
//			}
//		} catch (Exception e) {
//			throw new XxlConfException(e);
//		}
//	}
//
//	/**
//	 * set data to node (watch)
//     *
//	 * @param path
//	 * @param data
//	 * @return
//	 */
//	public Stat setPathData(String path, String data, boolean watch) {
//		try {
//			Stat stat = getClient().exists(path, watch);
//			if (stat == null) {
//				createPathWithParent(path, watch);
//				stat = getClient().exists(path, watch);
//			}
//			return getClient().setData(path, data.getBytes("UTF-8"), stat.getVersion());
//		} catch (Exception e) {
//			throw new XxlConfException(e);
//		}
//	}
//
//	/**
//	 * get data from node (watch)
//	 *
//	 * @param path
//	 * @return
//	 */
//	public String getPathData(String path, boolean watch){
//		try {
//			String znodeValue = null;
//			Stat stat = getClient().exists(path, watch);
//			if (stat != null) {
//				byte[] resultData = getClient().getData(path, watch, null);
//				if (resultData != null) {
//					znodeValue = new String(resultData, "UTF-8");
//				}
//			} else {
//				logger.info(">>>>>>>>>> xxl-conf, path[{}] not found.", path);
//			}
//			return znodeValue;
//		} catch (Exception e) {
//			throw new XxlConfException(e);
//		}
//	}
//
//
//}