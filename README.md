<p align="center">
    <img src="https://www.xuxueli.com/doc/static/xxl-job/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-CONF</h3>
    <p align="center">
        XXL-CONF is a distributed service management platform, that acts as a configuration center and a service registry.
        <br>
        <a href="https://www.xuxueli.com/xxl-conf/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://github.com/xuxueli/xxl-conf/actions">
            <img src="https://github.com/xuxueli/xxl-conf/workflows/Java%20CI/badge.svg" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-conf/badge.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-conf/releases">
            <img src="https://img.shields.io/github/release/xuxueli/xxl-conf.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-conf/">
            <img src="https://img.shields.io/github/stars/xuxueli/xxl-conf" >
        </a>
        <a href="https://hub.docker.com/r/xuxueli/xxl-conf-admin/">
            <img src="https://img.shields.io/docker/pulls/xuxueli/xxl-conf-admin" >
        </a>
        <a href="http://www.gnu.org/licenses/gpl-3.0.html">
         <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
        </a>
        <a href="https://www.xuxueli.com/page/donate.html">
            <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square" >
        </a>
    </p>    
</p>

## Introduction
XXL-CONF is a distributed service management platform, that acts as a configuration center and service registry.
providing dynamic configuration management, service registration and discovery and other core capabilities. with "lightweight, second-level real-time push, multi-environment, cross-language, cross-room, permission control" and other features. Now open source, out of the box.

XXL-CONF 是一个 分布式服务管理平台，作为服务 配置中心 与 注册中心，提供 动态配置管理、服务注册与发现 等核心能力；拥有 “轻量级、秒级实时推送、多环境、跨语言、跨机房、权限控制” 等特性。现已开放源代码，开箱即用。


## Documentation
- [中文文档](https://www.xuxueli.com/xxl-conf/)

## Communication    
- [社区交流](https://www.xuxueli.com/page/community.html)

## Features

#### configuration center（配置中心 视角）
- 1、简单易用: 接入灵活方便，一分钟上手；
- 2、轻量级: 仅依赖DB无其他三方依赖，搭建部署及接入简单，一分钟上手；
- 3、高可用/HA：配置中心支持集群部署，提升配置中心系统容灾和可用性；
- 4、高性能:得益于配置中心与客户端的本地缓存以及多级缓存设计，因此配置读取性能非常高；单机可承担高并发配置读取； 
- 5、实时性: 借助内部广播机制，新服务上线、下线等变更，可以在1s内推送给客户端；
- 6、线上化管理: 配置中心提供线上化管理界面, 通过Web UI在线操作配置数据，直观高效；
- 8、动态更新：配置数据变更后，客户端配置数据会实时动态更新、并生效，不需要重启服务机器；
- 9、最终一致性：底层借助内置广播机制，保障配置数据的最终一致性，从而保证配置数据的同步；
- 10、多数据类型配置：支持多种数据类型配置，如：String、Boolean、Short、Integer、Long、Float、Double 等；
- 11、丰富配置接入方式：支持 "API、 注解、Listener" 等多种方式获取配置，可灵活选择使用；
- 12、配置变更监听功能：支持自定义Listener逻辑，监听配置变更事件，可据此动态刷新JDBC连接池等高级功能；
- 13、多环境支持：支持自定义环境（命名空间），管理多个环境的的配置数据；环境之间相互隔离；
- 14、跨语言/OpenAPI：提供语言无关的 配置中心 OpenAPI（RESTFUL 格式），提供拉取配置与实时感知配置变更能力，实现多语言支持；
- 15、跨机房：得益于配置中心系统设计，服务端为无状态服务，集群各节点提供对等的服务；因此异地跨机房部署时，只需要请求本机房配置中心即可，实现异地多活；
- 16、客户端断线重连强化：底层设计守护线程，周期性检测客户端连接、配置同步，提高异常情况下配置稳定性和时效性；
- 17、空配置处理：主动缓存null或不存在类型配置，避免配置请求穿透到远程配置Server引发雪崩问题；
- 18、访问令牌（AccessToken）：为提升系统安全性，服务端和客户端进行安全性校验，双方AccessToken匹配才允许通讯；
- 19、用户管理：支持在线添加和维护用户，包括普通用户和管理员两种类型用户，灵活管控系统权限；
- 20、配置权限控制；以项目为维度进行配置权限控制，管理员拥有全部项目权限，普通用户只有分配才拥有项目下配置的查看和管理权限；
- 21、历史版本回滚：配置变更后及时记录配置变更历史，支持历史配置版本对比及快速回溯；
- 22、配置快照：客户端从配置中心获取到的配置数据后，会周期性缓存到本地快照文件中，当从配置中心获取配置失败时，将会使用使用本地快照文件中的配置数据；提高系统可用性；
- 23、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现产品开箱即用；

#### registry center（注册中心 视角）
- 1、简单易用: 接入灵活方便，一分钟上手；
- 2、轻量级: 仅依赖DB无其他三方依赖，搭建部署及接入简单，一分钟上手；
- 3、高可用/HA：注册中心支持集群部署，提升注册中心系统容灾和可用性；
- 4、高性能:得益于注册中心与客户端的本地缓存以及多级缓存设计，因此注册数据读取性能非常高；单机可承担高并发配置读取；
- 5、实时性: 借助内部广播机制，新服务上线、下线等变更，可以在1s内推送给客户端；
- 6、多环境支持：支持自定义环境（命名空间），管理多个环境的的服务注册数据；环境之间相互隔离；
- 7、跨语言/OpenAPI：提供语言无关的 注册中心 OpenAPI（RESTFUL 格式），提供服务 注册、注销、心跳、查询 等能力，实现多语言支持；
- 8、跨机房：得益于注册中心系统设计，服务端为无状态服务，集群各节点提供对等的服务；因此异地跨机房部署时，只需要请求本机房配置中心即可，实现异地多活；
- 9、多状态：服务内置多状态，支持丰富业务使用场景。正常状态=支持动态注册、发现，服务注册信息实时更新；锁定状态=人工维护注册信息，服务注册信息固定不变；禁用状态=禁止使用，服务注册信息固定为空；
- 10、访问令牌（AccessToken）：为提升系统安全性，服务端和客户端进行安全性校验，双方AccessToken匹配才允许通讯；
- 11、用户管理：支持在线添加和维护用户，包括普通用户和管理员两种类型用户，灵活管控系统权限；
- 12、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现产品开箱即用；

## Development
于2015年，我在github上创建XXL-CONF项目仓库并提交第一个commit，随之进行系统结构设计，UI选型，交互设计……

至今，XXL-CONF已接入多家公司的线上产品线，接入场景如电商业务，O2O业务和核心中间件配置动态化等，截止2018-03-15为止，XXL-CONF已接入的公司包括不限于：

    - 1、深圳市绽放工场科技有限公司
	- 2、深圳双猴科技有限公司
	- 3、商智神州软件有限公司
	- 4、浙江力太科技
	- ……

> 更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-conf/issues/2 ) 登记，登记仅仅为了产品推广。

欢迎大家的关注和使用，XXL-CONF也将拥抱变化，持续发展。


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-conf/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-conf/issues/) 讨论新特性或者变更。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the donation amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )

无论捐赠金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
