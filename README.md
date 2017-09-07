<p align="center">
    <a href="http://www.xuxueli.com/xxl-conf/">
        <img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/xxl-logo.jpg" width="150">
    </a>
    <h3 align="center">XXL-CONF | 分布式配置管理平台</h3>
    <p align="center">
        XXL-CONF, A distributed configuration management platform.
        <br>
        <a href="http://www.xuxueli.com/xxl-conf/"><strong>-- Browse website. --</strong></a>
        <br>
        <br>
        <a href="https://travis-ci.org/xuxueli/xxl-conf">
            <img src="https://travis-ci.org/xuxueli/xxl-conf.svg?branch=master" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/badge.svg" >
        </a>
         <a href="https://github.com/xuxueli/xxl-conf/releases">
             <img src="https://img.shields.io/github/release/xuxueli/xxl-conf.svg" >
         </a>
         <a href="http://www.gnu.org/licenses/gpl-3.0.html">
             <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
         </a>
    </p>    
</p>

## 简介
XXL-CONF is a distributed configuration management platform.    
It's core design goal is "provide uniform configuration management for distributed systems".    
Now, it's already open source, and many companies use it in production environments, real "out-of-the-box".

XXL-CONF 是一个分布式配置管理平台，其核心设计目标是“为分布式业务提供统一的配置管理服务”。现已开放源代码并接入多家公司线上产品线，开箱即用。


## 文档
- [中文文档](http://www.xuxueli.com/xxl-conf/)

## 特性
- 1、简单易用: 上手非常简单, 只需要引入maven依赖和一行配置即可;
- 2、在线管理: 提供配置管理中心, 支持在线管理配置信息;
- 3、实时推送: 配置信息更新后, Zookeeper实时推送配置信息, 项目中配置数据会实时更新并生效, 不需要重启线上机器;
- 4、高性能: 系统会对Zookeeper推送的配置信息, 在Encache中做本地缓存, 在接受推送更新或者缓存失效时会及时更新缓存数据, 因此业务中对配置数据的查询并不存在性能问题;
- 5、配置备份: 配置数据首先会保存在Zookeeper中, 同时, 在MySQL中会对配置信息做备份, 保证配置数据的安全性;
- 6、HA: 配置中心基于Zookeeper集群, 只要集群节点保证存活数量大于N/2+1, 就可保证服务稳定, 避免单点风险;
- 7、分布式: 可方便的接入线上分布式部署的各个业务线, 统一管理配置信息;
- 8、配置共享: 平台中的配置信息针对各个业务线是平等的, 各个业务线可以共享配置中心的配置信息, 当然也可以配置业务内专属配置信息;
- 9、配置分组: 支持对配置进行分组管理, 每条配置将会生成全局唯一标示GroupKey,在client端使用时,需要通过该值匹配对应的配置信息;


## 背景

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

##### 博客地址 (将会在两个博客同步更新文档)

- [oschina地址](http://my.oschina.net/xuxueli/blog/734267)
- [cnblogs地址](http://www.cnblogs.com/xuxueli/p/4777808.html)

## 技术交流

- [社区交流](http://www.xuxueli.com/page/community.html)


## 项目贡献
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-conf/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-conf/issues/) 讨论新特性或者变更。

## 接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-conf/issues/2 ) 登记，登记仅仅为了产品推广。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

## 捐赠
No matter how much the amount is enough to express your thought, thank you very much ：）

无论金额多少都足够表达您这份心意，非常感谢 ：）

微信：<img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/donate-wechat.png" width="200">
支付宝：<img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/donate-alipay.jpg" width="200">
