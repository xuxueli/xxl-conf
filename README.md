# 《分布式配置管理平台XXL-CONF》
## 一、简介

#### 1.1 概述
XXL-CONF 是一个分布式配置管理平台，其核心设计目标是“为分布式业务提供统一的配置管理服务”。现已开放源代码，开箱即用。

#### 1.2 特性
- 1、在线管理: XXL-CONF平台中的配置信息, 支持在线查看和管理;
- 2、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 3、高性能: XXL-CONF会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 4、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 5、HA: XXL-CONF基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 6、分布式: XXL-CONF支持方便的接入线上各个业务线;
- 7、低侵入性: XXL-CONF与系统耦合极低, 只需要引入maven依赖和一行配置即可;
- 8、配置共享: XXL-CONF平台中的配置信息, 多个项目可以共享;

#### 1.3 下载
源码地址 (将会在两个git仓库同步发布最新代码)
- [github地址](https://github.com/xuxueli/xxl-conf)
- [git.oschina地址](https://git.oschina.net/xuxueli0323/xxl-conf)

博客地址
- [oschina地址](http://my.oschina.net/xuxueli/blog/734267)

技术交流群(仅作技术交流)：367260654    [![image](http://pub.idqqimg.com/wpa/images/group.png)](http://shang.qq.com/wpa/qunwpa?idkey=4686e3fe01118445c75673a66b4cc6b2c7ce0641528205b6f403c179062b0a52 )

#### 1.4 环境
- Maven3+
- Jdk1.7+
- Tomcat7+
- Mysql5.5+

## 二、快速入门

#### 2.1 初始化“数据库”
请下载项目源码并解压，获取 "调度数据库初始化SQL脚本"(脚本文件为: 源码解压根目录xxl-conf/db/xxl-conf.sql) 并执行即可。


#### 2.2 编译源码
解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可，源码结构如下图所示：

![输入图片说明](https://static.oschina.net/uploads/img/201608/17202150_YgLy.png "在这里输入图片标题")

- xxl-conf-admin：配置管理中心
- xxl-conf-core：公共依赖
- xxl-conf-example: 接入XXl-CONF的Demo项目

#### 2.3 配置部署“配置管理中心”

    项目：xxl-conf-admin
    作用：查询和管理线上配置信息
    
- **A：配置“Zookeeper地址列表”**：

    - 配置文件位置: /data/webapps/xxl-conf.properties     (使用硬盘绝对路径, 好处是: 该机器上所有项目可共享该Zookeeper地址配置)
    - 配置文件内容:
    ```
    // 支持zookeeper地址集群配置, 如有多个地址用逗号分隔
    zkserver=127.0.0.1:2181
    ```
    - 配置文件作用: 配置Zookeeper的地址信息
    
- **B：配置“JDBC链接”**：


    配置文件位置: 源码/xxl-conf/xxl-conf-admin/src/main/resources/jdbc.properties
    作用: 配置数据在数据库中的备份
    
    
- **C：配置“登录账号和密码”**：

    配置文件位置: 源码/xxl-conf/xxl-conf-admin/src/main/resources/config.properties


#### 2.4 配置部署“接入XXL-CONF的Demo项目”

    项目：xxl-conf-example
    作用：供用户参考学习如何接入XXL-CONF
    
- **A：配置“Zookeeper地址列表”**：

    - 配置文件位置: /data/webapps/xxl-conf.properties     (使用硬盘绝对路径, 好处是: 该机器上所有项目可共享该Zookeeper地址配置)
    - 配置文件内容:
    ```
    // 支持zookeeper地址集群配置, 如有多个地址用逗号分隔
    zkserver=127.0.0.1:2181
    ```
    - 配置文件作用: 配置Zookeeper的地址信息
    
- **B：配置“XXL-CONF配置解析器”**：

    - 配置文件地址: 源码/xxl-conf/xxl-conf-example/src/main/resources/applicationcontext-xxl-conf.xml
    - 配置内容: 
    ```
    <!-- XXL-CONF配置解析器 -->
    <bean id="xxlConfPropertyPlaceholderConfigurer" class="com.xxl.conf.core.spring.XxlConfPropertyPlaceholderConfigurer" />
    ```
    
#### 2.5 新增配置信息

登录"配置管理中心"

![输入图片说明](https://static.oschina.net/uploads/img/201608/17204649_Lo7W.png "在这里输入图片标题")

进入"配置管理界面",点击"新增配置"按钮

![输入图片说明](https://static.oschina.net/uploads/img/201608/17204953_DMHC.png "在这里输入图片标题")

在弹出界面,填写配置信息

![输入图片说明](https://static.oschina.net/uploads/img/201608/17205147_7SBG.png "在这里输入图片标题")

至此, 一条配置信息已经添加完成,

#### 2.6 项目中使用XXL-CONF 

    项目: xxl-conf-example:   (可以参考 com.xxl.conf.example.controller.IndexController.index() )
    作用: 接入XXl-CONF的Demo项目


- 方式1: XML文件中的占位符方式
```
<bean id="configuration" class="com.xxl.conf.example.core.constant.Configuration">
    <property name="paramByXml" value="${key01}" />
</bean>
```

    
    特点:
    - 上面配置说明: 在项目启动时, Configuration的paramByXml属性, 会根据配置的占位符${key01}, 去XXL-CONF中匹配KEY=key01的配置信息, 赋值给paramByXml;
    - 目前, 该方式配置信息, 只会在项目启动时从XXL-CONF中加载一次, 项目启动后该值不会变更。 例如配置数据连接信息, 如果XXL-CONF平台中连接地址配置改边, 需要重启后才生效;
    - 该方式, 底层本质上是通过 "方式2: API方式" 实现的。

- 方式2: API方式
```
String paramByClient = XxlConfClient.get("key02", null);
```

    
    特点:
    - 上面代码说明: 会获取XXL-CONF平台中KEY=key02的配置信息, 如果不存在值使用传递的默认值;
    - 因为Zookeeper会实时推送配置更新到客户端, 因此该方法放回的值可以XXL-CONF平台中的值保持实时一致;
    - XXL-CONF会对Zookeeper推送的配置信息做本地缓存, 该方法查询的是缓存的配置信息, 因此该方法并不会产生性能问题, 使用时不需要考虑性能问题;

## 三、配置管理
略

## 四、总体设计

#### 4.1 架构图

进行中

#### 4.2 源码目录介绍

    - /db :“数据库”建表脚本
    - xxl-conf-admin：配置管理中心
    - xxl-conf-core：公共依赖
    - xxl-conf-example: 接入XXl-CONF的Demo项目

#### 4.3 核心思想

    - 统一维护配置信息;
    - 配置更新, 实时推送生效;

#### 规划中
- 1、zookeeper客户端,改用zkclientr ；

## 五、历史版本
#### 版本1.1.0
时间：2016年7月下旬；

特性：
- 1、在线管理: XXL-CONF平台中的配置信息, 支持在线查看和管理;
- 2、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 3、高性能: XXL-CONF会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 4、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 5、HA: XXL-CONF基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 6、分布式: XXL-CONF支持方便的接入线上各个业务线;
- 7、低侵入性: XXL-CONF与系统耦合极低, 只需要引入maven依赖和一行配置即可;
- 8、配置共享: XXL-CONF平台中的配置信息, 多个项目可以共享;

## 六、其他

#### 6.1 报告问题
XXL-CONF托管在Github上，如有问题可在 [ISSUES](https://github.com/xuxueli/xxl-conf/issues) 上提问，也可以加入技术交流群(仅作技术交流)：367260654

#### 6.2 接入登记
更多接入公司，欢迎在github [登记](https://github.com/xuxueli/xxl-conf/issues/2 )

