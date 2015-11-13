# 分布式配置管理平台xxl-cfg
github地址：https://github.com/xuxueli/xxl-cfg

git.osc地址：http://git.oschina.net/xuxueli0323/xxl-cfg

博客地址(内附使用教程)：http://www.cnblogs.com/xuxueli/p/4777808.html

邮件：931591021@qq.com

《分布式配置管理平台xxl-cfg》
==============================================
	基于zookeeper实现的配置管理平台，实时推送配置变更

两层数据存储：
------------------------
	1、zookeeper目录节点存储系统：/data/appcfg/xxl-cfg.properties
	2、sqlite3内嵌数据库：/data/appcfg/xxl-cfg.db

存储结构1.0
------------------------
	服务器端：一个zk集群 + 一个zk路径  = 一套zk配置 = 一套sqlite配置	：/data/appcfg/xxl-cfg.properties
	客户端：一个zk集群 + 一个zk路径 = 一套client配置					：/data/appcfg/xxl-cfg.properties

存储结构2.0（ing）
------------------------
	服务器端：一个zk集群 = 三套zk配置(beta、qa、product) + 一套sqlite配置	：/data/appcfg/xxl-cfg.properties	zkserver
	客户端：一个zk集群 + 一个zk路径 = 一套client配置							：/data/appcfg/xxl-cfg.properties	zkserver + deployenv

使用步骤(详细版)：
------------------------
	步骤一： 部署zookeeper集群：对配置中心提供服务器支持;
	
	步骤二： 搭建“配置管理中心”：对配置进行常规CRUD;
		1、zk配置：(/data/appcfg/xxl-cfg.properties); (可以从xxl-cfg-admin\src\test\resources目录下copy)
			文件内容如下:
			# zk配置存储目录，相对于/xxl-cfg目录。只允许为：beta默认、qa、product
			deployenv=qa
			# zookeeper集群地址列表，宕机数不允许超过n/2n+1
			zkserver=ip1:port1,ip2:port2,ip3:port3
		2、sqlite3内嵌数据库：(/data/appcfg/xxl-cfg.db);   (可以从xxl-cfg-admin\src\test\resources目录下copy)
			文件内容如下：
			表user_name：维护登陆账号，默认为xuxueli/123456
			表znode_entity：存储zookeeper中配置在sqlite中的一份备份;
		3、编译xxl-cfg-admin项目,导出为WAR包发布即可。至此，配置管理中心已经OK了;
	
	步骤三： 项目中使用xxl-cfg;
		1、zk配置：(/data/appcfg/xxl-cfg.properties); (可以从xxl-cfg-admin\src\test\resources目录下copy)
			文件内容如下:
			# zk配置存储目录，相对于/xxl-cfg目录。只允许为：beta、qa、product
			deployenv=qa
			# zookeeper集群地址列表，宕机数不允许超过n/2n+1
			zkserver=ip1:port1,ip2:port2,ip3:port3
		2、引入xxl-cfg-client依赖；
				<dependency>
					<groupId>com.xxl</groupId>
					<artifactId>xxl-cfg-client</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>
		3、非spring占位符方式：如，项目service逻辑中，针对某一个活动的开关配置；
				使用实例：ZkCfgLocalCache.get("xxl-cfg-demo.key01", null);
		4、spring占位符方式：如，项目启动参数，数据库连接地址、邮箱地址等等；
			<bean id="xxlCfgPostProcessor" class="com.xxl.cfg.spring.XxlCfgPostProcessor">
				<property name="localPropPath" value="local.properties" />
			</bean>
			参数local.properties为选填参数，当其中参数和zk中配置冲突时，优先使用local配置，例如本地调试开发场景；
				
使用步骤(简介版)：
------------------------
	步骤一： 部署zookeeper集群环境;
	步骤二： 搭建“配置管理中心”;
		1、zk配置文件：/data/appcfg/xxl-cfg.properties);		(可以从xxl-cfg-admin\src\test\resources目录下copy)
		2、sqlite3内嵌数据库：(/data/appcfg/xxl-cfg.db);   	(可以从xxl-cfg-admin\src\test\resources目录下copy)
		3、编译xxl-cfg-admin项目并导出为WAR包发布。
	步骤三： 项目中使用xxl-cfg;
		1、zk配置文件：/data/appcfg/xxl-cfg.properties);		(可以从xxl-cfg-admin\src\test\resources目录下copy)
		2、引入xxl-cfg-client依赖；
		3、非spring占位符方式：ZkCfgLocalCache.get("xxl-cfg-demo.key01", null);
		4、spring占位符方式：配置xxlCfgPostProcessor；