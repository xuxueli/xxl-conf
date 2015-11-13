package com.xxl.cfg.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.cfg.util.Environment;

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
public class XxlCfgClient implements Watcher {
	private static Logger logger = LoggerFactory.getLogger(XxlCfgClient.class);
	private static String deployenvPath = Environment.getDeployenvPath();;
	public static XxlCfgClient client = new XxlCfgClient();	// 注意静态变量,初始化顺序
	
	private ZooKeeper zooKeeper;
	public XxlCfgClient(){
		try {
			this.zooKeeper = new ZooKeeper(Environment.getZkserver(), 2000, this);
			this.create(deployenvPath);	// init cfg root path
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监控所有被触发的事件(One-time trigger)
	 */
	@Override
	public void process(WatchedEvent event) {
		try {
			logger.info(">>>>>>>>>> watcher:{}", BeanUtils.describe(event));
			
			EventType eventType = event.getType();
			if (eventType == EventType.None) {
				// TODO
			} else if (eventType == EventType.NodeCreated) {
				String znodePath = event.getPath();
				this.zooKeeper.exists(znodePath, true);	// add One-time trigger, ZooKeeper的Watcher是一次性的，用过了需要再注册
				
				String znodeKey = generateZnodeKeyFromPath(znodePath);
				if (znodeKey == null) {
					return;
				}
				String znodeValue = this.getData(znodeKey);
				
				XxlCfgLocalCache.put(znodeKey, znodeValue);
				String localValue = XxlCfgLocalCache.get(znodeKey, null);
				
				logger.info(">>>>>>>>>> 新增配置：zk:[{}:{}]", new Object[]{znodeKey, znodeValue});
				logger.info(">>>>>>>>>> 新增配置：local:[{}:{}]", new Object[]{znodeKey, localValue});
			} else if (eventType == EventType.NodeDeleted) {
				String znodePath = event.getPath();
				this.zooKeeper.exists(znodePath, true);
				
				String znodeKey = generateZnodeKeyFromPath(znodePath);
				String znodeValue = this.getData(znodeKey);
				
				XxlCfgLocalCache.remove(znodeKey);
				String localValue = XxlCfgLocalCache.get(znodeKey, null);
				
				logger.info(">>>>>>>>>> 删除配置：zk:[{}:{}]", new Object[]{znodeKey, znodeValue});
				logger.info(">>>>>>>>>> 删除配置：local:[{}:{}]", new Object[]{znodeKey, localValue});
			} else if (eventType == EventType.NodeDataChanged) {
				String znodePath = event.getPath();
				this.zooKeeper.exists(znodePath, true);
				
				String znodeKey = generateZnodeKeyFromPath(znodePath);
				String znodeValue = this.getData(znodeKey);
				
				XxlCfgLocalCache.put(znodeKey, znodeValue);
				String localValue = XxlCfgLocalCache.get(znodeKey, null);
				
				logger.info(">>>>>>>>>> 更新配置:zk：[{}:{}]", new Object[]{znodeKey, znodeValue});
				logger.info(">>>>>>>>>> 更新配置：local:[{}:{}]", new Object[]{znodeKey, localValue});
			} else if (eventType == EventType.NodeChildrenChanged) {
				// TODO
			}
			
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------------------
	/**
	 * generate znodeKey from znodePath
	 * @param znodePath
	 * @return ZnodeKey
	 */
	private String generateZnodeKeyFromPath(String znodePath){
		if (znodePath.length() <= deployenvPath.length()) {
			return null;
		}
		return znodePath.substring(deployenvPath.length()+1, znodePath.length());
	}
	
	/**
	 * generate path by key
	 * @param ZnodeKey
	 * @return znodePath
	 */
	private String generateZnodePathFromKey(String ZnodeKey){
		return deployenvPath + "/" + ZnodeKey;
	}
	
	/**
	 * generate parent znodePath from znodePath
	 * @param znodePath	: must more than two level 
	 * @return parent znodePath
	 */
	private String generateParentPath(String znodePath){
		String parentPath = null;
		if (znodePath != null && znodePath.lastIndexOf("/") != -1 && znodePath.lastIndexOf("/") > 0) {
			parentPath = znodePath.substring(0, znodePath.lastIndexOf("/"));
		}
		return parentPath;
	}
	
	/**
	 * create new node
	 * @param znodePath	(循环创建,因为parentPath不存在会保存)
	 * @param znodeValue
	 */
	private Stat create(String znodePath){
		try {
			Stat stat = this.zooKeeper.exists(znodePath, true);
			if (stat == null) {
				//  create parent znodePath
				String parentPath = generateParentPath(znodePath);
				if (StringUtils.isNotBlank(parentPath)) {
					Stat parentStat = this.zooKeeper.exists(parentPath, true);
					if (parentStat == null) {
						this.create(parentPath);
					}
				}
				zooKeeper.create(znodePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			return this.zooKeeper.exists(znodePath, true);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * delete exist node
	 * @param znodeKey
	 */
	public void delete(String znodeKey){
		String znodePath = this.generateZnodePathFromKey(znodeKey);
		try {
			Stat stat = this.zooKeeper.exists(znodePath, true);
			if (stat != null) {
				this.zooKeeper.delete(znodePath, stat.getVersion());
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", znodeKey);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * set data to node
	 * @param zooKeeper
	 * @param znodePath
	 * @param znodeValue
	 * @return
	 */
	public Stat setData(String znodeKey, String znodeValue) {
		String znodePath = this.generateZnodePathFromKey(znodeKey);
		try {
			Stat stat = this.zooKeeper.exists(znodePath, true);
			if (stat == null) {
				this.create(znodePath);
				stat = this.zooKeeper.exists(znodePath, true);
			}
			return zooKeeper.setData(znodePath, znodeValue.getBytes(),stat.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * get data from node
	 * @param znodeKey
	 * @return
	 */
	public String getData(String znodeKey){
		String znodePath = this.generateZnodePathFromKey(znodeKey);
		String znodeValue = null;
		try {
			Stat stat = this.zooKeeper.exists(znodePath, true);
			if (stat != null) {
				byte[] resultData = this.zooKeeper.getData(znodePath, this, null);
				if (resultData != null) {
					znodeValue = new String(resultData);
				}
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", znodeKey);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return znodeValue;
	}
	
	/**
	 * 获取配置目录下所有配置
	 * @return
	 */
	public Map<String, String> getAllData(){
		Map<String, String> addData = new HashMap<String, String>();
		try {
			List<String> nodeNameList = this.zooKeeper.getChildren(deployenvPath, true);
			if (CollectionUtils.isNotEmpty(nodeNameList)) {
				for (String znodeKey : nodeNameList) {
					String znodeValue = this.getData(znodeKey);
					addData.put(znodeKey, znodeValue);
				}
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return addData;
	}
	
	public static void main(String[] args) throws InterruptedException, KeeperException {
		/*while (true) {
			String znodeKey = "key01";
			String value = "value" + new Random().nextInt(1000);
			logger.info(">>>>>>>>>> ########## client set, {}:{}", new Object[]{znodeKey, value});
			
			ZkCfgClient.client.setData(znodeKey, value );
			//ZkCfgClient.client.delete(znodeKey);
			
			logger.info(">>>>>>>>>> ########## cache get, {}:{}", znodeKey, ZkCfgLocalCache.get(znodeKey));
			System.out.println();
			TimeUnit.SECONDS.sleep(5);
		}*/
		/*Map<String, String> addData =client.getAllData();
		System.out.println("-----------------------");
		for (Entry<String, String> item : addData.entrySet()) {
			System.out.println(item.getKey() + ":" + item.getValue());
		}*/
		Stat stat = client.zooKeeper.exists(deployenvPath, true);
		client.zooKeeper.delete(deployenvPath, stat.getVersion());
	}

}