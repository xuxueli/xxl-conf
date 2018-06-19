<p align="center">
    <img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-CONF</h3>
    <p align="center">
        XXL-CONF, A distributed configuration management platform.
        <br>
        <a href="http://www.xuxueli.com/xxl-conf/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://travis-ci.org/xuxueli/xxl-conf">
            <img src="https://travis-ci.org/xuxueli/xxl-conf.svg?branch=master" >
        </a>
        <a href="https://hub.docker.com/r/xuxueli/xxl-conf-admin/">
            <img src="https://img.shields.io/badge/docker-passing-brightgreen.svg" >
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
        <a href="http://www.xuxueli.com/page/donate.html">
            <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square" >
        </a>
    </p>    
</p>

## Introduction
XXL-CONF is a distributed configuration management platform, with features such as "millisecond dynamic push, multi-environment, multi-language, configuration listening, permission control, version rollback".
Now, it's already open source, real "out-of-the-box".

XXL-CONF 是一个分布式配置管理平台，拥有"强一致性、毫秒级动态推送、多环境、多语言、配置监听、权限控制、版本回滚"等特性。现已开放源代码，开箱即用。

## Documentation
- [中文文档](http://www.xuxueli.com/xxl-conf/)

## Features
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


## Development
于2015年，我在github上创建XXL-CONF项目仓库并提交第一个commit，随之进行系统结构设计，UI选型，交互设计……

至今，XXL-CONF已接入多家公司的线上产品线，接入场景如电商业务，O2O业务和核心中间件配置动态化等，截止2018-03-15为止，XXL-CONF已接入的公司包括不限于：

    - 1、深圳市绽放工场科技有限公司
	- 2、深圳双猴科技有限公司
	- ……

> 更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-conf/issues/2 ) 登记，登记仅仅为了产品推广。

欢迎大家的关注和使用，XXL-CONF也将拥抱变化，持续发展。

## Communication

- [社区交流](http://www.xuxueli.com/page/community.html)


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-conf/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-conf/issues/) 讨论新特性或者变更。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the donation amount is enough to express your thought, thank you very much ：）     [To donate](http://www.xuxueli.com/page/donate.html )

无论捐赠金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](http://www.xuxueli.com/page/donate.html )
