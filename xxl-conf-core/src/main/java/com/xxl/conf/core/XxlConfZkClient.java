package com.xxl.conf.core;

import com.xxl.conf.core.util.Environment;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * ZooKeeper cfg client (Watcher + some utils)
 * @author xuxueli 2015年8月26日21:36:43
 *
 *         Zookeeper
 *         从设计模式角度来看，是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理大家都关心的数据，然后接受观察者的注册
 *         ，一旦这些数据的状态发生变化，Zookeeper 就将负责通知已经在 Zookeeper
 *         上注册的那些观察者做出相应的反应，从而实现集群中类似 Master/Slave 管理模式
 *
 *         1、统一命名服务（Name Service）:将有层次的目录结构关联到一定资源上，广泛意义上的关联，也许你并不需要将名称关联到特定资源上，
 *         你可能只需要一个不会重复名称。
 *
 *         2、配置管理（Configuration Management）：分布式统一配置管理：将配置信息保存在
 *         Zookeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到
 *         Zookeeper 的通知，然后从 Zookeeper 获取新的配置信息应用到系统中
 *
 *         3、集群管理（Group
 *         Membership）:Zookeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，那么必须
 *         要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，集群中其它集群必须知道，从而做出调整重新分配服务策略。
 *         同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道。
 *
 *         4、共享锁（Locks）：
 *         5、队列管理：a、当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。b、队列按照 FIFO 方式
 *         进行入队和出队操作，例如实现生产者和消费者模型。
 *
 *         集中式配置管理 动态更新
 *
 */
public class XxlConfZkClient implements Watcher {
	private static Logger logger = LoggerFactory.getLogger(XxlConfZkClient.class);

	// ------------------------------ zookeeper client ------------------------------
	private static ZooKeeper zooKeeper;
	private static ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock(true);
	private static ZooKeeper getInstance(){
		if (zooKeeper==null) {
			try {
				if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
					try {
						zooKeeper = new ZooKeeper(Environment.ZK_ADDRESS, 20000, new Watcher() {
							@Override
							public void process(WatchedEvent watchedEvent) {
								try {
									logger.info(">>>>>>>>>> xxl-conf: watcher:{}", watchedEvent);

									// session expire, close old and create new
									if (watchedEvent.getState() == Event.KeeperState.Expired) {
										zooKeeper.close();
										zooKeeper = null;
										getInstance();
									}

									String path = watchedEvent.getPath();
									String key = pathToKey(path);
									if (key != null) {
										// add One-time trigger
										zooKeeper.exists(path, true);
										if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
											XxlConfClient.remove(key);
										} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
											String data = getPathDataByKey(key);
											XxlConfClient.update(key, data);
										}
									}
								} catch (KeeperException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						});
						XxlConfZkClient.createWithParent(Environment.CONF_DATA_PATH);	// init cfg root path
					} finally {
						INSTANCE_INIT_LOCK.unlock();
					}
                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (zooKeeper == null) {
			throw new NullPointerException(">>>>>>>>>>> xxl-cache, XxlConfZkClient.zooKeeper is null.");
		}
		return zooKeeper;
	}

	/**
	 * 监控所有被触发的事件(One-time trigger)
	 */
	@Override
	public void process(WatchedEvent event) {

	}

	// ------------------------------ util ------------------------------
	/**
	 * path 2 key
	 * @param nodePath
	 * @return ZnodeKey
	 */
	private static String pathToKey(String nodePath){
		if (nodePath==null || nodePath.length() <= Environment.CONF_DATA_PATH.length() || !nodePath.startsWith(Environment.CONF_DATA_PATH)) {
			return null;
		}
		return nodePath.substring(Environment.CONF_DATA_PATH.length()+1, nodePath.length());
	}

	/**
	 * key 2 path
	 * @param nodeKey
	 * @return znodePath
	 */
	private static String keyToPath(String nodeKey){
		return Environment.CONF_DATA_PATH + "/" + nodeKey;
	}

	public static String generateGroupKey(String nodeGroup, String nodeKey){
		return nodeGroup + "." + nodeKey;
	}

	/**
	 * create node path with parent path (如果父节点不存在,循环创建父节点, 因为父节点不存在zookeeper会抛异常)
	 * @param path	()
	 */
	private static Stat createWithParent(String path){
		// valid
		if (path==null || path.trim().length()==0) {
			return null;
		}

		try {
			Stat stat = getInstance().exists(path, true);
			if (stat == null) {
				//  valid parent, createWithParent if not exists
				if (path.lastIndexOf("/") > 0) {
					String parentPath = path.substring(0, path.lastIndexOf("/"));
					Stat parentStat = getInstance().exists(parentPath, true);
					if (parentStat == null) {
						createWithParent(parentPath);
					}
				}
				// create desc node path
				zooKeeper.create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			return getInstance().exists(path, true);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * delete path by key
	 * @param key
	 */
	public static void deletePathByKey(String key){
		String path = keyToPath(key);
		try {
			Stat stat = getInstance().exists(path, true);
			if (stat != null) {
				getInstance().delete(path, stat.getVersion());
			} else {
				logger.info(">>>>>>>>>> zookeeper node path not found :{}", key);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set data to node
	 * @param key
	 * @param data
	 * @return
	 */
	public static Stat setPathDataByKey(String key, String data) {
		String path = keyToPath(key);
		try {
			Stat stat = getInstance().exists(path, true);
			if (stat == null) {
				createWithParent(path);
				stat = getInstance().exists(path, true);
			}
			return zooKeeper.setData(path, data.getBytes(),stat.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get data from node
	 * @param key
	 * @return
	 */
	public static String getPathDataByKey(String key){
		String path = keyToPath(key);
		try {
			Stat stat = getInstance().exists(path, true);
			if (stat != null) {
				String znodeValue = null;
				byte[] resultData = getInstance().getData(path, true, null);
				if (resultData != null) {
					znodeValue = new String(resultData);
				}
				return znodeValue;
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", key);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取配置目录下所有配置
	 * @return
	 */
	private static Map<String, String> getAllData(){
		Map<String, String> allData = new HashMap<String, String>();
		try {
			List<String> childKeys = getInstance().getChildren(Environment.CONF_DATA_PATH, true);
			if (childKeys!=null && childKeys.size()>0) {
				for (String key : childKeys) {
					String data = getPathDataByKey(key);
					allData.put(key, data);
				}
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return allData;
	}

	public static void main(String[] args) throws InterruptedException, KeeperException {
		setPathDataByKey("key02", "666");
		System.out.println(getPathDataByKey("key02"));

		System.out.println(getAllData());
		getInstance().delete(Environment.CONF_DATA_PATH + "/key02", -1);
		getInstance().delete(Environment.CONF_DATA_PATH, -1);

	}

}