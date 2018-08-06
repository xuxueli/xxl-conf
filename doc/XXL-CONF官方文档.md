## 《分布式配置管理平台XXL-CONF》

[![Build Status](https://travis-ci.org/xuxueli/xxl-conf.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-conf)
[![Docker Status](https://img.shields.io/badge/docker-passing-brightgreen.svg)](https://hub.docker.com/r/xuxueli/xxl-conf-admin/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-conf.svg)](https://github.com/xuxueli/xxl-conf/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](http://www.xuxueli.com/page/donate.html)


## 一、简介

### 1.1 概述
XXL-CONF 是一个分布式配置管理平台，拥有"强一致性、毫秒级动态推送、多环境、多语言、配置监听、权限控制、版本回滚"等特性。现已开放源代码，开箱即用。

### 1.2 特性
- 1、简单: 部署简单、接入灵活方便，一分钟上手；
- 2、在线管理: 提供配置中心, 通过Web界面在线操作配置数据，直观高效；
- 3、多环境支持：单个配置中心集群，支持自定义多套环境，管理多个环境的的配置数据；环境之间相互隔离；
- 4、多数据类型配置：支持多种数据类型配置，如：String、Boolean、Short、Integer、Long、Float、Double 等；
- 5、多语言支持（配置中心Agent服务）：提供配置中心Agent服务，可据此通过Http（long-polling）获取配置数据并实时感知配置变更，从而实现多语言支持。
- 6、配置变更监听功能：可开发Listener逻辑，监听配置变更事件，可据此动态刷新JDBC连接池等高级功能；
- 7、毫秒级动态推送: 配置更新后, 实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 8、强一致性：保障配置数据的强一致性，提高配置时效性；
- 9、配置中心HA：配置中心支持集群部署，提供系统可用性；
- 10、推送服务HA: 配置服务基于ZK集群, 只要集群节点保证存活数量大于N/2N+1, 就可保证服务稳定, 避免单点风险;
- 11、配置备份: 配置数据同时在ZK与MySQL中存储和备份， 提高配置数据的安全性;
- 12、多种获取配置方式：支持 "API、 注解、XML占位符" 等多种方式获取配置，可灵活选择使用；
- 13、兼容Spring原生配置：兼容Spring原生配置方式 "@Value"、"${}" 加载本地配置功能；与分布式配置获取方式隔离，互不干扰； 
- 14、分布式: 支持多业务线接入并统一管理配置信息，支撑分布式业务场景;
- 15、项目隔离: 以项目为维度管理配置, 方便隔离不同业务线配置;
- 16、高性能: 通过Ehcache对配置数据做Local Cache, 提高性能;
- 17、客户端断线重连强化：设置守护线程，周期性检测客户端连接、配置同步，提高异常情况下配置稳定性和时效性；
- 18、空配置处理：主动缓存null或不存在类型配置，避免配置请求穿透到远程配置Server引发雪崩问题；
- 19、用户管理：支持在线添加和维护用户，包括普通用户和管理员两种类型用户；
- 20、配置权限控制；以项目为维度进行配置权限控制，管理员拥有全部项目权限，普通用户只有分配才拥有项目下配置的查看和管理权限；
- 21、历史版本回滚：记录配置变更历史，方便历史配置版本回溯，默认记录10个历史版本；
- 22、配置同步：全量检测未同步配置项，使用DB中配置备份数据覆盖ZK中配置数据并推送更新；在配置中心异常恢复、新配置中心集群初始化等场景中十分有效。
- 23、配置快照：客户端从配置中心获取到的配置数据后，会周期性缓存到本地快照文件中，当从配置中心获取配置失败时，将会使用使用本地快照文件中的配置数据；提高系统可用性；


### 1.3 背景

> why not properties

常规项目开发过程中, 通常会将配置信息位于在项目resource目录下的properties文件文件中, 配置信息通常包括有: jdbc地址配置、redis地址配置、活动开关、阈值配置、黑白名单……等等。使用properties维护配置信息将会导致以下几个问题:

- 1、需要手动修改properties文件; 
- 2、需要重新编译打包; 
- 3、需要重启线上服务器 (项目集群时,更加令人崩溃) ; 
- 4、配置生效不及时: 因为流程复杂, 新的配置生效需要经历比较长的时间才可以生效;
- 5、不同环境上线包不一致: 例如JDBC连接, 不同环境需要差异化配置;

> why XXL-CONF

- 1、不需要 (手动修改properties文件) : 在配置中心提供的Web界面中, 定位到指定配置项, 输入新的配置的值, 点击更新按钮即可;
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
[http://gitee.com/xuxueli0323/xxl-conf](http://gitee.com/xuxueli0323/xxl-conf) | [Download](http://gitee.com/xuxueli0323/xxl-conf/releases)

 
#### 中央仓库地址
```
<dependency>
  <groupId>com.xuxueli</groupId>
  <artifactId>xxl-conf-core</artifactId>
  <version>{最新稳定版}</version>
</dependency>
```

#### 技术交流

- [社区交流](http://www.xuxueli.com/page/community.html)

### 1.5 环境
- Maven3+
- Jdk1.7+
- Zookeeper3.4+
- Mysql5.6+


## 二、快速入门

### 2.1 环境准备

#### 初始化“数据库”

请下载项目源码并解压，获取 "数据库初始化SQL脚本（Mysql）" 并执行即可。脚本位置如下：
 
    xxl-conf/doc/db/xxl-conf.sql
    
#### 初始化"ZK集群"
配置推送基于zookeeper实现，请准备一个稳定的ZK集群。


### 2.2 编译源码
解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可，源码结构如下图所示：

    - xxl-conf-admin：配置中心
    - xxl-conf-core：公共依赖
    - xxl-conf-samples: 接入XXl-CONF的示例项目，供用户参考学习
        - xxl-conf-sample-spring：spring版本
        - xxl-conf-sample-springboot：springboot版本
        - xxl-conf-sample-jfinal：jfinal版本
        - xxl-conf-sample-nutz：nutz版本

### 2.3 “配置中心” 搭建（支持集群）

    项目：xxl-conf-admin
    作用：提供一个完善强大的配置管理平台，包含：环境管理、用户管理、项目管理、配置管理等功能，全部操作通过Web界面在线完成；
    

#### 方式1：源码编译方式搭建：
    
- 配置文件位置：

```
/xxl-conf/xxl-conf-admin/src/main/resources/application.properties
```
    
- 配置项说明：

```
# xxl-conf, zookeeper 地址，如有多个地址用逗号分隔；
xxl.conf.zkaddress=${zkaddress:127.0.0.1:2181}
# xxl-conf, zookeeper 的digest权限信息；
xxl.conf.zkdigest=${zkdigest:}

# xxl-conf, jdbc 
spring.datasource.url=jdbc:mysql://${mysqladdress:127.0.0.1:3306}/xxl-conf?Unicode=true&amp;characterEncoding=UTF-8
spring.datasource.username=${mysqlusername:root}
spring.datasource.password=${mysqlpassword:root_pwd}
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

- 配置中心启动：   

项目编译打包后，可直接通过命令行启动；

```
// 方式1：使用默认配置，mysql与zk默认为本地地址；
java -jar xxl-conf-admin.jar

// 方式2：支持自定义 mysql与zk 地址；
java -jar xxl-conf-admin.jar --mysqladdress=127.0.0.1:3306 --mysqlusername=root --mysqlpassword=root_pwd --zkaddress=127.0.0.1:2181
```

#### 方式2：Docker 镜像方式搭建：

- 下载镜像
```
// Docker地址：https://hub.docker.com/r/xuxueli/xxl-conf-admin/
docker pull xuxueli/xxl-conf-admin
```

- 创建容器并运行
```
// 可通过 "PARAMS" 支持自定义 mysql与zk 地址；
docker run -e PARAMS="--mysqladdress=127.0.0.1:3306 --zkaddress=127.0.0.1:2181" -p 8080:8080 -v /tmp:/data/applogs --name xxl-conf-admin  -d xuxueli/xxl-conf-admin
```

#### "配置中心" 集群：

配置中心支持集群部署，提高配置中心负载能力和可用性。  
配置中心集群部署时，项目配置文件保持一致即可。


### 2.4 “接入XXL-CONF的示例项目” 项目配置

    项目：xxl-conf-sample-springboot
    作用：接入XXl-CONF的示例项目，供用户参考学习。这里以 springboot 版本进行介绍，其他版本可参考各自sample项目。

#### A、引入maven依赖
```
<!-- xxl-conf-client -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-conf-core</artifactId>
    <version>{最新稳定版}</version>
</dependency>
```

#### B、添加“XXL-CONF 配置信息”

可参考配置文件：
```
/xxl-conf/xxl-conf-samples/xxl-conf-sample-springboot/src/main/resources/application.properties
```

配置项说明
```
# 配置中心zookeeper集群地址，如有多个地址用逗号分隔；
xxl.conf.zkaddress=127.0.0.1:2181
# 配置zookeeper的digest权限信息；
xxl.conf.zkdigest=
# 环境配置，如"test、ppe、product"等，指定配置加载环境；
xxl.conf.env=test
# 配置快照文件地址，非空时启用快照功能；会周期性缓存到本地快照文件中，当从配置中心获取配置失败时，将会使用使用本地快照文件中的配置数据；提高系统可用性；
xxl.conf.mirrorfile=/data/applogs/xxl-conf/xxl-conf-mirror.properties
```

#### C、设置“XXL-CONF 配置工厂”

可参考配置文件：
```
/xxl-conf/xxl-conf-samples/xxl-conf-sample-springboot/src/main/java/com/xxl/conf/sample/config/XxlConfConfig.java
```

配置项说明
```
@Bean
public XxlConfFactory xxlConfFactory() {

    XxlConfFactory xxlConf = new XxlConfFactory();
    xxlConf.setZkaddress(zkaddress);
    xxlConf.setZkdigest(zkdigest);
    xxlConf.setEnv(env);
    xxlConf.setMirrorfile(mirrorfile);

    logger.info(">>>>>>>>>>> xxl-conf config init.");
    return xxlConf;
}
```

至此，配置完成。


### 2.5 功能测试

#### a、添加和更新配置
参考章节 "4.2 配置管理" 添加或更新配置信息； 

#### b、获取配置并接受动态推送更新
参考章节 "三、客户端配置获取" 获取配置并接受动态推送更新；


## 三、客户端配置获取
XXL-CONF 提供多种配置方式，包括 "API、 @XxlConf、XML" 等多种配置方式，介绍如下。

> 可参考项目 "xxl-conf-sample-spring"（接入XXl-CONF的示例项目，供用户参考学习），代码位置：com.xxl.conf.sample.controller.IndexController.index() 


### 3.1 方式1: API方式
参考 "IndexController" 代码如下：
```
String paramByApi = XxlConfClient.get("default.key01", null);
```
- 用法：代码中直接调用API即可，示例代码 ""XxlConfClient.get("key", null)"";
- 优点：
    - 配置从配置中心自动加载；
    - 存在LocalCache，不用担心性能问题；
    - 支持动态推送更新；
    - 支持多数据类型；


### 3.2 方式2: @XxlConf 注解方式
参考 "DemoConf.paramByAnno" 属性配置；示例代码 
```
@XxlConf("default.key02")
public String paramByAnno;
```
- 用法：对象Field上加注解 ""@XxlConf("key")"，支持设置默认值，支持设置是否开启动态刷新；
- 优点：
    - 配置从配置中心自动加载；
    - 存在LocalCache，不用担心性能问题；
    - 支持动态推送更新；
    - 支持设置配置默认值；
    - 可配置是否开启 "动态推送更新";
        
“@XxlConf”注解属性 | 说明
--- | ---
value | 配置Key
defaultValue | 配置为空时的默认值
callback | 配置更新时，是否需要同步刷新配置


### 3.3 方式3: XML占位符方式
参考 "applicationcontext-xxl-conf.xml" 中 "DemoConf.paramByXml" 属性配置；示例代码如下：
```
<bean id="demoConf" class="com.xxl.conf.sample.demo.DemoConf">
    <property name="paramByXml" value="$XxlConf{default.key03}" />
</bean>
```
- 用法：占位符方式 "$XxlConf{key}"；
- 优点：
    - 配置从配置中心自动加载；
    - 存在LocalCache，不用担心性能问题；
    - 支持动态推送更新；

### 3.4 方式4: "XML + API" 混合方式
参考如下代码：
```
<bean id="demoConf" class="com.xxl.conf.sample.demo.DemoConf2">
    <constructor-arg index="0" value="#{T(com.xxl.conf.core.XxlConfClient).get('key')}" />
    <property name="paramByXml" value="#{T(com.xxl.conf.core.XxlConfClient).get('default.key03')}" />
</bean>
```

- 用法：占位符方式 "#{T(com.xxl.conf.core.XxlConfClient).get('key')}"；
- 优点：
    - 配置从配置中心自动加载；
    - 存在LocalCache，不用担心性能问题；
    - 兼容性好：在一些特殊的XML配置加载场景，如 "XML构造器传参"、"自定义spring的schema/xsd" ，上述几种方式不适用，此时可以考虑这种方式，兼容各种场景格式；
- 缺点：
    - 不支持动态推送更新；
    

### 3.5 其他方式: 配置变更监听
可开发Listener逻辑，监听配置变更事件；可据此实现动态刷新JDBC连接池等高级功能；

参考 "IndexController" 代码如下：
```
XxlConfClient.addListener("default.key01", new XxlConfListener(){
    @Override
    public void onChange(String key, String value) throws Exception {
        logger.info("配置变更事件通知：{}={}", key, value);
    }
});
```

## 四、管理中心操作指南

### 4.1、环境管理

进入 "环境管理" 界面，可自定义和管理环境信息。   
单个配置中心集群，支持自定义多套环境，管理多个环境的的配置数据；环境之间相互隔离；

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_01.png "在这里输入图片标题")

新增环境：点击 "新增环境" 按钮可添加新的环境配置，环境属性说明如下：

    - Env：每个环境拥有一个维护的Env，作为环境标识；
    - 环境名称：该环境的名称；

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_02.png "在这里输入图片标题")

环境切换：配置中心顶部菜单展示当前操作的配置中心环境，可通过该菜单切换不同配置中心环境，从而管理不同环境中的配置数据；

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_03.png "在这里输入图片标题")

### 4.2、用户（权限）管理

进入 "用户管理" 界面，可查看配置中心中所有用户信息。
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_04.png "在这里输入图片标题")

新增用户：点击 "新增用户" 按钮，可添加新用户，用户属性说明如下：

    - 权限：
        - 管理员：拥有配置中心所有权限，包括：用户管理、环境管理、项目管理、配置管理等；
        - 普通用户：仅允许操作自己拥有权限的项目下的配置；
    - 用户名：配置中心登陆账号
    - 密码：配置中心登陆密码
    
系统默认提供了一个管理员用户和一个普通用户。
    
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_E0cT.png "在这里输入图片标题")

分配项目权限：选中普通用户，点击右侧 "分配项目权限" 按钮，可为用户分配项目权限。拥有项目权限后，该用户可以查看和操作该项目下全部配置数据。

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_GaLm.png "在这里输入图片标题")

修改用户密码：配置中心右上角下拉框，点击 "修改密码" 按钮，可修改当前登录用户的登录密码
（除此之外，管理员用户，可通过编辑用户信息功能来修改其他用户的登录密码）；
    
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_syzc.png "在这里输入图片标题")


### 4.3、项目管理

系统以 "项目" 为维度进行权限控制，以及配置隔离。可进入 "配置管理界面" 操作和维护项目，项目属性说明如下：

    - AppName：每个项目拥有唯一的AppName，作为项目标识，同时作为该项目下配置的统一前缀；
    - 项目名称：该项目的名称；

系统默认提供了一个示例项目。

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_05.png "在这里输入图片标题")

### 4.4 配置管理

进入"配置管理" 界面, 选择项目，然后可查看和操作该项目下配置数据。

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_06.png "在这里输入图片标题")

新增配置：点击 "新增配置" 按钮可添加配置数据，配置属性说明如下：

    - KEY：配置的KEY，创建时将会自动添加所属项目的APPName所谓前缀，生成最终的Key。可通过客户端使用最终的Key获取配置；
    - 描述：该配置的描述信息；
    - VALUE：配置的值；

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_d5ak.png "在这里输入图片标题")

至此, 一条配置信息已经添加完成；       
通过客户端可以获取该配置, 并且支持动态推送更新。 

历史版本回滚：配置存在历史变更操作时，点击右侧的 "变更历史" 按钮，可查看该配置的历史变更记录。
包括操作时间、操作人，设置的配置值等历史数据，因此可以根据历史数据，重新编辑配置并回滚到历史版本；

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_Whz5.png "在这里输入图片标题")


## 五、总体设计

### 5.1 架构图

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_07.png "在这里输入图片标题")

### 5.2 "配置中心" 设计

配置中心由以下几个核心部分组成：

- 1、管理平台：提供一个完善强大的配置管理平台，包含：环境管理、用户管理、项目管理、配置管理等功能，全部操作通过Web界面在线完成；
- 2、管理平台DB：存储配置信息备份、配置的版本变更信息等，进一步保证数据的安全性；同时也存储"管理平台"中多个模块的底层数据；
- 3、ZK集群：配置中心在ZK集群中维护一个根目录 每新增一条配置项, 将会在该目录下新增一个子节点。当配置变更时将会触发ZK节点的变更, 并将触发对应类型的ZK广播；
- 4、客户端：可参考章节 "5.3 客户端 设计" ；
- 5、配置中心Agent服务：可选部分，可参考章节 "5.5 配置Agent服务（多语言支持）"；

### 5.3 "客户端" 设计

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-conf/master/doc/images/img_08.png "在这里输入图片标题")

客户端基于多层设计，核心四层设计如下:

- 1、API层：提供业务方可直接使用的上层API, 简单易用, 一行代码获取配置信息；同时保证配置的实时性、高性能;
- 2、Ehcache层：客户端的Local Cache，极大提升API层的性能，降低对ZK集群的压力；首次加载配置、监听配置变更、底层异步周期性同步配置时，将会写入或更新缓存；
- 3、ZK-Client层：ZK远程客户端的封装，用于加载远程配置、通过NodeDataChanged监听配置变更，提高配置时效性；
- 4、Mirror-File层：配置数据的本地快照文件，会周期性同步 "Ehcache层" 中的配置数据写入到 "Mirror-File" 中；当无法从配置中心获取配置，如ZK宕机时，将会使用 "Mirror-File" 中的配置数据，提高系统的可用性；

得益于客户端的多层设计，以及 LocalCache 和 Mirror-File 等特性，因此业务方可以在高QPS、高并发场景下使用XXL-CONF的客户端, 不必担心并发压力或ZK宕机导致系统问题。

### 5.4 配置中心接入方式

#### a、Client方式：
应用通过内嵌和依赖Client端的方式，直连配置中心；此时系统结构分层如下：

- 接入方应用：内嵌Client端，直连配合中心ZK，获取配置，动态watch配置变更；
- 配置中心集群：托管配置，配置同步至ZK集群；

优点：
- 实时性：配置变更实时推送；

缺点：
- 语言限制：目前仅提供Java语言Client端；

#### b、Agent方式：
在配置中心与接入方应用之间，部署 "配置中心Agent服务"（参考 "5.5 配置Agent服务（多语言支持）"），应用通过 "配置中心Agent服务" 获取配置；此时系统结构分层如下：

- 接入方应用：以Http方式从 "配置中心Agent服务" 获取配置。通过 "周期性轮训" 或者 "long-polling" 方式感知配置变更；
- 配置中心Agent服务：内嵌Client端，直连配合中心ZK，获取配置，动态watch配置变更；并提送配置加载的Agent服务；
- 配置中心集群：托管配置，配置同步至ZK集群；

优点：
- 多语言支持：支持通过Http方式获取多个配置数据，无语言限制；

缺点：
- 实时性：配置变更依赖 "周期性轮训" 或者 "long-polling"；


### 5.5 配置Agent服务（多语言支持）

Java应用可通过 "Client方式" 方便的获取配置中心的数据；

非Java语言应用，提供 "配置中心Agent服务" 获取配置中心配置；"配置中心Agent服务" 本质是一个Http接口，支持同步、异步（long-polling）两种Http请求方式；可据此获取配置数据并实时感知配置变更，从而实现多语言支持。

"配置中心Agent服务" 存在Ehcache缓存性能极高，并且支持集群横向扩展；

"配置中心Agent服务" 可参考以下代码：  
（项目 "xxl-conf-sample-springboot" 本身提供 "配置中心Agent服务" 功能，可直接部署该项目使用；）
```
/xxl-conf/xxl-conf-samples/xxl-conf-sample-springboot/src/main/java/com/xxl/conf/sample/controller/XxlConfAgentController.java
``` 

"配置Agent服务" Http接口文档如下：
```
// Http接口地址格式
http://{Agent部署路径}/xxlconfagent

// 请求参数，get/post方式均可
accessToken :   请求Token，进行安全严重，需要和 "配置Agent服务" 内部保持一致；
confKeys    :	配置Key，多个逗号分隔
async	    :	trne=同步请求，立即返回 "confKeys" 对应的配置信息；false（默认）=异步请求，监听 "confKeys" 对应的配置发生变更后，才会返回发生变更的配置信息；

// 响应数据格式
{
    "code":200,     // 200 表示正常、其他失败
    "msg":null,     // 错误提示消息
    "content":{     // 配置信息，KV格式
        "key01": "value01",
        "key02": "value02"
    }
}

```

### 5.6 配置同步功能 
进入配置管理界面，点击 "全量同步" 按钮可触发该功能。

将会检测对应项目下的全部未同步配置项，使用DB中配置数据覆盖ZK中配置数据并推送更新；

该功能在配置中心异常恢复、新配置中心集群初始化等场景中十分有效。

### 5.7 配置快照功能
客户端从配置中心获取到的配置数据后，会周期性缓存到本地快照文件中，当从配置中心获取配置失败时，将会使用使用本地快照文件中的配置数据；提高系统可用性；

### 5.8 多环境支持
单个配置中心集群，支持自定义多套环境，管理多个环境的的配置数据；环境之间相互隔离；

此处给出一些多环境配置的建议：
- 机器资源紧缺、系统规模较小时：建议部署单个配置中心集群，比如部署 "配置中心集群"，通过定义多套环境，如 "dev、test、ppe、product" 隔离不同环境配置数据；优点是，可以同享配置中心资源；
- 机器资源充足、系统规模较大时：建议部署多个配置中心集群，比如部署 "配置中心集群A"，定义环境 "ppe、product"；部署 "配置中心集群B"，定义环境 "dev、test"等；优点是，可以避免多个集群相互影响；


## 六、历史版本
### 6.1 版本 v1.0.0 特性[2015-11-13]
- 初始版本导入;

### 6.2 版本 v1.1.0 特性[2016-08-17]
- 1、简单易用: 上手非常简单, 只需要引入maven依赖和一行配置即可;
- 2、在线管理: 提供配置中心, 支持在线管理配置信息;
- 3、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 4、高性能: 系统会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 5、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 6、HA: 配置中心基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 7、分布式: 可方便的接入线上分布式部署的各个业务线, 统一管理配置信息;
- 8、配置共享: 平台中的配置信息针对各个业务线是平等的, 各个业务线可以共享配置中心的配置信息, 当然也可以配置业务内专属配置信息;

### 6.3 版本 v1.2.0 新特性[2016-10-08]
- 1、配置分组: 支持对配置进行分组管理, 每条配置将会生成全局唯一标示GroupKey,在client端使用时,需要通过该值匹配对应的配置信息;

### 6.4 版本 v1.3.0 新特性[2016-10-08]
- 1、支持在线维护配置分组；
- 2、项目groupId从com.xxl迁移至com.xuxueli，为推送maven中央仓库做准备；
- 3、v1.3.0版本开始，推送公共依赖至中央仓库；

### 6.5 版本 v1.3.1-beta 新特性[2017-08-10]
- 1、本地配置优先加载逻辑调整；
- 2、zookeeper地址方式从磁盘迁移至项目内；

### 6.6 版本 v1.3.1-beta2 新特性[2017-08-19]
- 1、配置文件统一问题fix；

### 6.7 版本 v1.4.0 新特性[2018-03-02]
- 1、支持通过 "@XxlConf" 注解获取配置；
- 2、动态推送更新：目前支持 "XML、 @XxlConf、API" 三种配置方式，均支持配置动态刷新；
- 3、配置变更监听功能：可开发Listener逻辑，监听配置变更事件，可据此动态刷新JDBC连接池等高级功能；
- 4、用户管理：支持在线添加和维护用户，包括普通用户和管理员两种类型用户；
- 5、配置权限控制；以项目为维度进行配置权限控制，管理员拥有全部项目权限，普通用户只有分配才拥有项目下配置的查看和管理权限；
- 6、配置变更版本记录：记录配置变更历史，方便历史配置版本回溯，默认记录10个历史版本；
- 7、客户端断线重连强化，除了依赖ZK之外，新增守护线程，周期性刷新Local Cache中配置数据并watch，进一步提高配置时效性；
- 8、ZK过期重连时，主动刷新LocalCache中配置数据，提高异常情况下配置时效性；
- 9、ZK重入锁做二次校验，防止并发冲突；
- 10、主动缓存null或不存在类型配置，避免配置请求穿透到ZK引发雪崩问题；
- 11、Local Cache缓存长度固定为1000，采用LRU策略移除。
- 12、表结构优化；
- 13、重构核心代码，规范代码结构；
- 14、环境配置文件，支持自定义存放位置，项目resource下或磁盘目录下均可；
- 15、支持设置ZK中配置存储路径，方便实现多环境复用ZK集群；
- 16、用户在线修改密码；
- 17、升级依赖版本，如Ehcache、Spring等；
- 18、弹框插件改为使用Layui；
- 19、AdminLTE版本升级；
- 20、Sample项目目录结构规范；
- 21、新增SpringBoot类型Sample项目；

### 6.8 版本 v1.4.1 新特性[2018-04-12]
- 1、Ehcache缓存对象CacheNode序列化优化；
- 2、XML配置方式，Bean初始化时配置加载逻辑优化；
- 3、升级多项依赖至较新版本：spring、spring-boot、jackson、freemarker、mybatis等；

### 6.9 版本 v1.4.2 新特性[2018-05-30]
- 1、多环境支持：单个配置中心集群，支持自定义多套环境，管理多个环境的的配置数据；环境之间相互隔离；
- 2、多数据类型配置：支持多种数据类型配置，如：String、Boolean、Short、Integer、Long、Float、Double 等；
- 3、多语言支持：提供配置Agent服务，可据此通过Http获取配置数据，从而实现多语言支持。Agent存在Ehcache缓存性能极高，并且支持集群横向扩展；
- 4、新增 "Jfinal" 类型Sample项目；
- 5、新增 "Nutz" 类型Sample项目；
- 6、支持ZK鉴权信息配置；
- 7、Local Cache缓存长度扩充为100000，采用LRU过期策略。
- 8、配置数据强制编码 UTF-8，解决因操作系统编码格式不一致导致的配置乱码问题；
- 9、XxlConf与原生配置加载方式( "@Value"、"${...}" )兼容，相互隔离，互不影响；替代原LocalConf层；
- 10、移除Spring强制依赖。在保持对Spring良好支持情况下，提高对非Spring环境的兼容性；
- 11、容器组件初始化顺序调整，修复@PostConstruct无法识别问题；
- 12、配置优化，移除冗余配置项；
- 13、小概率情况下BeanRefresh重复刷新问题修复；
- 14、升级pom依赖至较新版本，如Spring、Zookeeper等；

### 6.10 版本 v1.5.0 新特性[2018-06-15]
- 1、配置中心Agent服务增强：针对非Java应用提供Agent服务获取配置，提供同步、异步两种Http请求方式，原生支持 long-polling（Http） 的方式获取配置数据、并实时感知配置变更。同时，强化请求权限校验；
- 2、配置同步功能：将会检测对应项目下的全部未同步配置项，使用DB中配置数据覆盖ZK中配置数据并推送更新；在配置中心异常恢复、新配置中心集群初始化等场景中十分有效；
- 3、配置快照：客户端从配置中心获取到的配置数据后，会周期性缓存到本地快照文件中，当从配置中心获取配置失败时，将会使用使用本地快照文件中的配置数据；提高系统可用性；
- 4、配置中心，迁移为spring boot项目；
- 5、配置中心，提供官方docker镜像；
- 6、Cglib代理情况下，如 "@Configuration" 注解，Bean无法注入配置问题修复；
- 7、springboot项目加载prop失败的问题修复；
- 8、升级多项maven依赖至较新版本，如spring等；

### 6.11 版本 v1.5.1 新特性[迭代中]
- 1、ftl变量判空问题修复；
- 2、配置快照文件生成时自动创建多层父目录；

### TODO LIST
- 1、本地优先配置：优先加载该配置中数据，常用于本地调试。早期版本功能实用性低，现已移除，考虑是否完全移除；
- 2、zookeeper客户端迁移至curator；暂时不考虑，自研client更可控；
- 3、轻量级改造：移除ZK，改为 "Server端广播 + long-polling" 方式实现，降低学习、部署成本；暂时不考虑，基于ZK实时性更高，强一致性；
- 4、注册中心特性：原生支持注册中心功能，强一致性推送注册信息；
- 5、分布式锁特性：原生支持分布式锁功能；



## 七、其他

### 7.1 项目贡献
欢迎参与项目贡献！比如提交PR修一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-conf/issues/) 讨论新特性或者变更。

### 7.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-conf/issues/2 ) 登记，登记仅仅为了产品推广。

### 7.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论捐赠金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](http://www.xuxueli.com/page/donate.html )
