## 《分布式配置管理平台XXL-CONF》

[![Build Status](https://travis-ci.org/xuxueli/xxl-conf.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-conf)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-conf.svg)](https://github.com/xuxueli/xxl-conf/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)


## 一、简介

### 1.1 概述
XXL-CONF 是一个分布式配置管理平台，其核心设计目标是“为分布式业务提供统一的配置管理服务”。现已开放源代码，开箱即用。

### 1.2 特性
- 1、简单易用: 上手非常简单, 只需要引入maven依赖和一行配置即可;
- 2、在线管理: 提供配置管理中心, 支持在线管理配置信息;
- 3、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 4、高性能: 系统会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 5、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 6、HA: 配置中心基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 7、分布式: 可方便的接入线上分布式部署的各个业务线, 统一管理配置信息;
- 8、配置共享: 平台中的配置信息针对各个业务线是平等的, 各个业务线可以共享配置中心的配置信息, 当然也可以配置业务内专属配置信息;
- 9、配置分组: 支持对配置进行分组管理, 每条配置将会生成全局唯一标示GroupKey,在client端使用时,需要通过该值匹配对应的配置信息;

### 1.3 背景

> why not properties

常规项目开发过程中, 通常会将配置信息位于在项目resource目录下的properties文件文件中, 配置信息通常包括有: jdbc地址配置、redis地址配置、活动开关、阈值配置、黑白名单……等等。使用properties维护配置信息将会导致以下几个问题:

- 1、需要手动修改properties文件; 
- 2、需要重新编译打包; 
- 3、需要重启线上服务器 (项目集群时,更加令人崩溃) ; 
- 4、配置生效不及时: 因为流程复杂, 新的配置生效需要经历比较长的时间才可以生效;
- 5、不同环境上线包不一致: 例如JDBC连接, 不同环境需要差异化配置;

> why XXL-CONF

- 1、不需要 (手动修改properties文件) : 在配置管理中心提供的Web界面中, 定位到指定配置项, 输入新的配置的值, 点击更新按钮即可;
- 2、不需要 (重新编译打包) : 配置更新后, 实时推送新配置信息至项目中, 不需要编译打包;
- 3、不需要 (重启线上服务器) : 配置更新后, 实时推送新配置信息至项目中, 实时生效, 不需要重启线上机器; (在项目集群部署时, 将会节省大量的时间, 避免了集群机器一个一个的重启, 费时费力)
- 4、配置生效 "非常及时" : 点击更新按钮, 新的配置信息将会即可推送到项目中, 瞬间生效, 非常及时。比如一些开关类型的配置, 配置变更后, 将会立刻推送至项目中并生效, 相对常规配置修改繁琐的流程, 及时性可谓天壤之别; 
- 5、不同环境 "同一个上线包" : 因为差异化的配置托管在配置中心, 因此一个上线包可以复用在生产、测试等各个运行环境, 提供能效;

### 1.4 下载

#### 文档地址

- [中文文档](http://www.xuxueli.com/xxl-conf/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-conf](https://github.com/xuxueli/xxl-conf) | [Download](https://github.com/xuxueli/xxl-conf/releases)  
[http://git.oschina.net/xuxueli0323/xxl-conf](http://git.oschina.net/xuxueli0323/xxl-conf) | [Download](http://git.oschina.net/xuxueli0323/xxl-conf/releases)

 
#### 中央仓库地址
```
<dependency>
  <groupId>com.xuxueli</groupId>
  <artifactId>xxl-conf-core</artifactId>
  <version>1.3.0</version>
</dependency>
```

#### 技术交流

- [社区交流](http://www.xuxueli.com/page/community.html)

### 1.5 环境
- Maven3+
- Jdk1.7+
- Tomcat7+
- Zookeeper3.4+
- Mysql5.5+

## 二、快速入门

### 2.1 初始化“数据库”
请下载项目源码并解压，获取 "调度数据库初始化SQL脚本" 并执行即可。脚本位置如下：
 
    xxl-conf/db/xxl-conf.sql

### 2.2 编译源码
解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可，源码结构如下图所示：

![输入图片说明](https://static.oschina.net/uploads/img/201608/17202150_YgLy.png "在这里输入图片标题")

- xxl-conf-admin：配置管理中心
- xxl-conf-core：公共依赖
- xxl-conf-example: 接入XXl-CONF的Demo项目

### 2.3 “配置管理中心” 项目配置

    项目：xxl-conf-admin
    作用：管理线上配置信息
    
配置文件位置：

    xxl-conf/xxl-conf-admin/src/main/resources/xxl-config-admin.properties
    
配置项目说明：
```
# xxl-conf, zk address  （配置中心zookeeper集群地址，如有多个地址用逗号分隔）
xxl.conf.zkserver=127.0.0.1:2181

# xxl-conf, jdbc    （配置中心mysql地址）
xxl.conf.jdbc.driverClass=com.mysql.jdbc.Driver
xxl.conf.jdbc.url=jdbc:mysql://localhost:3306/xxl-conf?Unicode=true&amp;characterEncoding=UTF-8
xxl.conf.jdbc.username=root
xxl.conf.jdbc.password=root_pwd

# xxl-conf, admin login （管理中心登录账号密码）
xxl.conf.login.username=admin
xxl.conf.login.password=123456
```

### 2.4 “接入XXL-CONF的Demo项目” 项目配置

    项目：xxl-conf-example
    作用：供用户参考学习如何接入XXL-CONF

#### A、引入maven依赖
```
<!-- xxl-conf-client -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-conf-core</artifactId>
    <version>${xxl.conf.version}</version>
</dependency>
```

#### B、配置 “XXL-CONF配置解析器”

可参考配置文件：

    /xxl-conf/xxl-conf-example/src/main/resources/spring/applicationcontext-xxl-conf.xml


配置项说明
```
<!-- XXL-CONF配置解析器 -->
<bean id="xxlConfPropertyPlaceholderConfigurer" class="com.xxl.conf.core.spring.XxlConfPropertyPlaceholderConfigurer" />
```

#### C、设置 "xxl-conf.properties" 

可参考配置文件：

    /xxl-conf/xxl-conf-example/src/main/resources/xxl-conf.properties

配置项说明
```
# xxl-conf, zk address  （配置中心zookeeper集群地址，如有多个地址用逗号分隔）
xxl.conf.zkserver=127.0.0.1:2181
```

该配置文件，除了支持配置ZK地址，还可以配置一些本地配置。
XXL-CONF 加载配置时会优先加载 "xxl-conf.properties" 中的配置, 然后才会加载ZK中的配置。可以将一些希望存放本地的配置存放在该文件。

### 2.5 新增配置分组

![输入图片说明](https://static.oschina.net/uploads/img/201610/08182521_r2e4.png "在这里输入图片标题")

每个配置分组对应一个唯一的GroupName，作为该分组下配置的统一前缀。在“分组管理”栏目可以创建并管理配置分组信息，系统已经提供一个默认分组.
   
### 2.6 新增配置信息

登录"配置管理中心"

![输入图片说明](https://static.oschina.net/uploads/img/201608/17204649_Lo7W.png "在这里输入图片标题")

进入"配置管理界面",点击"新增配置"按钮

![输入图片说明](https://static.oschina.net/uploads/img/201609/27103547_Ztff.png "在这里输入图片标题")

在弹出界面,填写配置信息

![输入图片说明](https://static.oschina.net/uploads/img/201609/27103706_iExP.png "在这里输入图片标题")

至此, 一条配置信息已经添加完成.

通过client端,可以实时获取配置信息, 通过本地已经加载过得配置将会接受Zookeeper的更新推送, 如下如日志:

![输入图片说明](https://static.oschina.net/uploads/img/201608/18111816_Of9e.png "在这里输入图片标题")

### 2.7 项目中使用XXL-CONF 

    项目: xxl-conf-example:   (可以参考 com.xxl.conf.example.controller.IndexController.index() )
    作用: 接入XXl-CONF的Demo项目


- 方式1: XML文件中的占位符方式
    ```
    <bean id="configuration" class="com.xxl.conf.example.core.constant.Configuration">
        <property name="paramByXml" value="${default.key01}" />
    </bean>
    ```
    特点:
    - 上面配置说明: 在项目启动时, Configuration的paramByXml属性, 会根据配置的占位符${default.key01}, 去XXL-CONF中匹配KEY=key01的配置信息, 赋值给paramByXml;
    - 目前, 该方式配置信息, 只会在项目启动时从XXL-CONF中加载一次, 项目启动后该值不会变更。 例如配置数据连接信息, 如果XXL-CONF平台中连接地址配置改边, 需要重启后才生效;
    - 该方式, 底层本质上是通过 "方式2: API方式" 实现的。

- 方式2: API方式
    ```
    String paramByClient = XxlConfClient.get("default.key02", null);
    ```
    特点:
    - 上面代码说明: 会获取XXL-CONF平台中KEY=default.key02的配置信息, 如果不存在值使用传递的默认值;
    - 因为Zookeeper会实时推送配置更新到客户端, 因此该方法放回的值可以XXL-CONF平台中的值保持实时一致;
    - XXL-CONF会对Zookeeper推送的配置信息做本地缓存, 该方法查询的是缓存的配置信息, 因此该方法并不会产生性能问题, 使用时不需要考虑性能问题;
    

## 三、总体设计

### 3.1 架构图

![输入图片说明](https://static.oschina.net/uploads/img/201609/13124946_jTID.jpg "在这里输入图片标题")

### 3.2 "配置项" 设计

系统配置信息以K/V的形式存在, "配置项" 属性如下:

- 分组: "配置项" 的分组, 便于配置分组管理;
- KEY : "配置项" 的全局唯一标识, 对应一条配置信息;
- VALUE : "配置项" 中保存的数据信息, 仅仅支持String字符串格式; 
- 描述 : 配置项的描述信息;

每条配置,将会生成全局唯一标示GroupKey,在client端使用时,需要通过该值匹配对应的配置信息;

### 3.3 "配置中心" 设计

![输入图片说明](https://static.oschina.net/uploads/img/201609/13165343_V4Mt.jpg "在这里输入图片标题")

- 1、ZK设计: 系统在ZK集群中占用一个根目录 "/xxl-conf", 每新增一条配置项, 将会在该目录下新增一个子节点。结构如下图, 当配置变更时将会触发ZK节点的变更, 将会触发对应类型的ZK广播。
- 2、数据库备份配置信息: 配置信息在ZK中的新增、变更等操作, 将会同步备份到Mysql中, 进一步保证数据的安全性;
- 3、配置推送: 配置推送功能在ZK的Watch机制实现。Client在加载一条配置信息时将会Watch该配置对应的ZK节点, 因此, 当对该配置项进行配置更新等操作时, 将会触发ZK的NodeDataChanged广播, Client竟会立刻得到通知并刷新本地缓存中的配置信息;

> ZK之watcher普及(来源官方文档,以及网络博客)

    1、可以注册watcher的方法：getData、exists、getChildren。
    2、可以触发watcher的方法：create、delete、setData。连接断开的情况下触发的watcher会丢失。
    3、一个Watcher实例是一个回调函数，被回调一次后就被移除了。如果还需要关注数据的变化，需要再次注册watcher。
    4、New ZooKeeper时注册的watcher叫default watcher，它不是一次性的，只对client的连接状态变化作出反应。(推荐ZK初始化时, 主动Watcher如exists)
    5、实现永久监听: 由于zookeeper是一次性监听，所以我们必须在wather的process方法里面再设置监听。
    6、getChildren("/path")监视/path的子节点，如果（/path）自己删了，也会触发NodeDeleted事件。


《操作--事件》 | event For “/path” | 	event For “/path/child”
--- | --- | ---
create(“/path”) | EventType.NodeCreated | 无
delete(“/path”) |   EventType.NodeDeleted | 无
setData(“/path”) |  EventType.NodeDataChanged | 无
create(“/path/child”) | EventType.NodeChildrenChanged（getChild） | EventType.NodeCreated
delete(“/path/child”) | EventType.NodeChildrenChanged（getChild） | EventType.NodeDeleted
setData(“/path/child”) | 无 | EventType.NodeDataChanged


《事件--Watch方式》 | Default Watcher | exists(“/path”) | getData(“/path”) | 	getChildren(“/path”)
--- | --- | --- | ---
EventType.None  | 触发 | 触发 | 触发 | 触发 
EventType.NodeCreated  |  | 触发 | 触发 |  
EventType.NodeDeleted  |  | 触发 | 触发 | 
EventType.NodeDataChanged  |  | 触发 | 触发 | 
EventType.NodeChildrenChanged  |  |  |  | 触发 

> ZooKeeper的一个性能测试

[测试数据来自阿里中间件团队](http://jm.taobao.org/2011/07/15/1070/)

ZK集群情况: 3台ZooKeeper服务器。8核64位jdk1.6；log和snapshot放在不同磁盘;

- 场景一: pub创建NODE,随后删除
    - 操作: 同一个目录下，先create EPHEMERAL node，再delete；create和delete各计一次更新。没有订阅。一个进程开多个连接，每个连接绑定一个线程，在多个path下做上述操作；不同的连接操作的path不同
    - 结果数据: "dataSize(字节)-TPS-响应时间(ms)" 统计结果为: 255-14723-82, 1024-7677-280, 4096-2037-1585;

- 场景二: pub创建NODE, sub订阅并获取数据
    - 操作: 一个进程开多个连接，每连接一个线程，每个连接在多个path下做下述操作；不同的连接操作的path不同。每个path有3个订阅者连接，一个修改者连接。先全部订阅好。然后每个修改者在自己的每个path下创建一个EPHEMERAL node，不删除；创建前记录时间，订阅者收到event后记录时间(eventStat)；重新get到数据后再记录时间(dataStat)。共1000个pub连接，3000个sub连接，20W条数据。收到通知后再去读取数据，五台4核client机器。
    - 结果汇总: getAfterNotify=false（只收事件，受到通知后不去读取数据）；五台4核client机器
    - 结果数据: "dataSize(字节)-TPS-响应时间(ms)" 统计结果为: 255-1W+-256ms, 1024-1W+-256, 2048-1W+-270, 4096-8000+-520;

- 场景三: pub创建NODE,随后设置数据
    - 一个进程开多个连接，每连接一个线程，每个连接在多个path下做下述操作；不同的连接操作的path不同。每个path有一个修改者连接，没有订阅者。每个修改者在自己的每个path下设置数据。
    - 结果汇总: getAfterNotify=false（只收事件，受到通知后不去读取数据）；五台4核client机器
    - 结果数据: "dataSize(字节)-TPS-响应时间(ms)" 统计结果为: 255-14723-82, 1024-7677-280, 4096-2037-1585 ;
    
总结: 由于一致性协议带来的额外网络交互，消息开销，以及本地log的IO开销，再加上ZK本身每1000条批量处理1次的优化策略，写入的平均响应时间总会在50-60ms之上。但是整体的TPS还是可观的。单个写入数据的体积越大，响应时间越长，TPS越低，这也是普遍规律了。压测过程中log文件对磁盘的消耗很大。实际运行中应该使用自动脚本定时删除历史log和snapshot文件。

### 3.4 "配置管理中心" 设计

"配置管理中心" 是 "配置中心" 的上层封装, 提供Web界面供用户对配置信息进行配置查询、配置新增、配置更新和配置删除等操作;

### 3.5 "客户端" 设计

![输入图片说明](https://static.oschina.net/uploads/img/201609/14111236_q8oi.jpg "在这里输入图片标题")

**API方式加载配置**: 客户端主要分为三层:

- ZK-Client : 第一层为ZK远程客户端的封装, 当业务方项目初始化某一个用到的配置项时, 将会触发ZK-Client对该配置对应节点的Watch, 因此当该节点变动时将会监听到ZK的类似NodeDataChanged的广播, 可以实时获取最新配置信息; 
- Ehcache : 第二层为客户端本地缓存, 可以大大提高系统的并发能力, 当配置初始化或者接受到ZK-Client的配置变更时, 将会把配置信息缓存只Encache中, 业务中针对配置的查询都是读缓存方式实现, 降低对ZK集群的压力;
- Client-API : 第三层为暴露给业务方使用API, 简单易用, 一行代码获取配置信息, 同时可保证API获取到的配置信息是实时最新的配置信息;

(API方式加载配置, 因为底层做了配置本地缓存, 因此可以放心应用在业务代码中, 不必担心并发压力。完整的支持配置实时推送更新)

![输入图片说明](https://static.oschina.net/uploads/img/201609/14111248_wzwN.jpg "在这里输入图片标题")

**Bean方式加载配置**: 

系统会在Spring容器中追加一个"PropertyPlaceholderConfigurer"属性解析器, 内部通过自定义的"StringValueResolver"解析器解析配置占位符 "${...}", 匹配到的配置信息将调用"XXL-CFONF"的API客户端加载最新配置信息进行Bean对象的属性赋值,最终完成实例化过程。

(Bean方式加载配置,仅仅在实例化时加载一次; 考虑都实例化后的对象通常为持久化对象, 如数据库连接池对象, 不建议配置的太灵活, 因此Bean类型配置更新需要重启机器)



## 四、历史版本
### 4.1 版本1.1.0新特性
- 1、简单易用: 上手非常简单, 只需要引入maven依赖和一行配置即可;
- 2、在线管理: 提供配置管理中心, 支持在线管理配置信息;
- 3、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 4、高性能: 系统会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 5、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 6、HA: 配置中心基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 7、分布式: 可方便的接入线上分布式部署的各个业务线, 统一管理配置信息;
- 8、配置共享: 平台中的配置信息针对各个业务线是平等的, 各个业务线可以共享配置中心的配置信息, 当然也可以配置业务内专属配置信息;

### 4.2 版本1.2.0新特性
- 1、配置分组: 支持对配置进行分组管理, 每条配置将会生成全局唯一标示GroupKey,在client端使用时,需要通过该值匹配对应的配置信息;

### 4.3 版本1.3.0新特性
- 1、支持在线维护配置分组；
- 2、项目groupId从com.xxl迁移至com.xuxueli，为推送maven中央仓库做准备；
- 3、v1.3.0版本开始，推送公共依赖至中央仓库；

### 4.4 版本1.3.1新特性(Coding)
- zookeeper地址方式从磁盘迁移至项目内；

### TODO LIST
- 1、权限管理：以分组为权限最小单元，只有分组的成员用户才有权限进行对应的配置操作；
- 2、zookeeper客户端迁移至curator；
- 3、local cache 备份到磁盘；zk异常且local properties未配置时，从磁盘上读取配置；
- 4、优化官方文档，制作项目网站；
- 5、zk客户端优化，强化断线重连 + getInstance做二次校验；
- 6、客户端断线重连强化，除了依赖ZK之外，定时守护线程周期性校验ZK状态。考虑是否周期性刷新缓存； 

## 五、其他

### 5.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-conf/issues/) 讨论新特性或者变更。

### 5.2 接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-conf/issues/2 ) 登记，登记仅仅为了产品推广。

---
## 捐赠
No matter how much the amount is enough to express your thought, thank you very much ：）

无论金额多少都足够表达您这份心意，非常感谢 ：）

微信：<img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/donate-wechat.png" width="200">
支付宝：<img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/donate-alipay.jpg" width="200">
