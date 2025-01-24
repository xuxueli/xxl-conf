#
# XXL-CONF
# Copyright (c) 2015-present, xuxueli.

CREATE database if NOT EXISTS `xxl_conf` default character set utf8mb4 collate utf8mb4_unicode_ci;
use `xxl_conf`;

SET NAMES utf8mb4;


## —————————————————————— config data ——————————————————

CREATE TABLE `xxl_conf_data` (
    `id`                 bigint(20)      NOT NULL AUTO_INCREMENT,
    `env`                varchar(10)     NOT NULL COMMENT 'Env（环境唯一标识）',
    `appname`            varchar(50)     NOT NULL COMMENT 'AppName（应用唯一标识）',
    `key`                varchar(200)    NOT NULL COMMENT '配置项Key',
    `value`              text            NOT NULL COMMENT '配置项Value',
    `desc`               varchar(100)    NOT NULL COMMENT '配置项描述',
    `add_time`           datetime        NOT NULL COMMENT '新增时间',
    `update_time`        datetime        NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_e_a_k` (`env`, `appname`, `key`) USING BTREE,
    KEY `i_e_a` (`env`, `appname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用配置数据';

CREATE TABLE `xxl_conf_data_log` (
    `id`                 bigint(20)      NOT NULL AUTO_INCREMENT,
    `data_id`            bigint(20)      NOT NULL COMMENT '配置数据ID',
    `value`              text            NOT NULL COMMENT '历史数据，配置项Value',
    `opt_username`       varchar(50)     NOT NULL COMMENT '操作人，账号',
    `add_time`           datetime        NOT NULL COMMENT '新增时间',
    `update_time`        datetime        NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `i_data_id` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用配置数据，操作日志';

## —————————————————————— registry ——————————————————

CREATE TABLE `xxl_conf_instance` (
   `id`                 bigint(20)      NOT NULL AUTO_INCREMENT,
   `env`                varchar(10)     NOT NULL COMMENT 'Env（环境唯一标识）',
   `appname`            varchar(50)     NOT NULL COMMENT 'AppName（应用唯一标识）',
   `ip`                 varchar(46)     NOT NULL COMMENT '注册节点IP',
   `port`               int(11)         NOT NULL COMMENT '注册节点端口号',
   `extend_info`        varchar(500)    DEFAULT NULL COMMENT '扩展信息',
   `register_model`     tinyint(4)      NOT NULL DEFAULT '0' COMMENT '注册模式：0-动态注册、1-持久化注册、2-禁用注册',
   `register_heartbeat` datetime        DEFAULT NULL COMMENT '节点最后心跳时间，动态注册时判定是否过期',
   `add_time`           datetime        NOT NULL COMMENT '新增时间',
   `update_time`        datetime        NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uni_instance` (`env`, `appname`, `ip`, `port`) USING BTREE,
   KEY `i_e_a` (`env`, `appname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用注册实例';

## —————————————————————— broadcast ——————————————————

CREATE TABLE `xxl_conf_message` (
    `id`            bigint(20)      NOT NULL AUTO_INCREMENT,
    `type`          tinyint(4)      NOT NULL COMMENT '消息类型：0-注册更新',
    `data`          text            NOT NULL COMMENT '消息正文，json结构体',
    `add_time`      datetime        NOT NULL COMMENT '新增时间',
    `update_time`   datetime        NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务注册变更消息';

## —————————————————————— env and app ——————————————————
CREATE TABLE `xxl_conf_application` (
                                        `id`            int(11)         NOT NULL AUTO_INCREMENT,
                                        `appname`       varchar(50)     NOT NULL COMMENT 'AppName（应用唯一标识）',
                                        `name`          varchar(20)     NOT NULL COMMENT '应用名称',
                                        `desc`          varchar(100)    NOT NULL COMMENT '应用描述',
                                        `add_time`      datetime        NOT NULL COMMENT '新增时间',
                                        `update_time`   datetime        NOT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `i_appname` (`appname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用';

CREATE TABLE `xxl_conf_environment` (
                                        `id`             int(11)         NOT NULL AUTO_INCREMENT,
                                        `env`            varchar(10)     NOT NULL COMMENT 'ENV（环境唯一标识）',
                                        `name`           varchar(20)     NOT NULL COMMENT '环境名称',
                                        `desc`           varchar(100)    NOT NULL COMMENT '环境描述',
                                        `add_time`       datetime        NOT NULL COMMENT '新增时间',
                                        `update_time`    datetime        NOT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `i_env` (`env`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境';

## —————————————————————— user and token ——————————————————
CREATE TABLE `xxl_conf_user` (
    `id`            int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      varchar(50) NOT NULL COMMENT '账号',
    `password`      varchar(50) NOT NULL COMMENT '密码',
    `user_token`    varchar(50) DEFAULT NULL COMMENT '登录token',
    `status`        tinyint(4)  NOT NULL COMMENT '状态：0-正常、1-禁用',
    `real_name`     varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `role`          varchar(20) NOT NULL COMMENT '角色：ADMIN-管理员，NORMAL-普通用户',
    `permission`    varchar(255) DEFAULT NULL COMMENT '权限：应用ID列表，多个逗号分割',
    `add_time`      datetime    NOT NULL COMMENT '新增时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `xxl_conf_access_token` (
   `id`             bigint(20)      NOT NULL AUTO_INCREMENT,
   `access_token`   varchar(50)     NOT NULL COMMENT '注册发现AccessToken',
   `status`         tinyint(4)      NOT NULL COMMENT '状态：0-正常、1-禁用',
   `add_time`       datetime        NOT NULL COMMENT '新增时间',
   `update_time`    datetime        NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='注册发现AccessToken';

## —————————————————————— init data ——————————————————
INSERT INTO `xxl_conf_user`(`id`, `username`, `password`, `user_token`, `status`, `real_name`, `role`, `add_time`, `update_time`)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '', 0, 'Jack', 'ADMIN', now(), now()),
       (2, 'user', 'e10adc3949ba59abbe56e057f20f883e', '', 0, 'Lucy', 'NORMAL', now(), now());
INSERT INTO `xxl_conf_access_token` (id, `access_token`, `status`, add_time, update_time)
VALUES (1, 'defaultaccesstoken', 0, now(), now());

INSERT INTO `xxl_conf_environment` (id, env, name, `desc`, add_time, update_time)
VALUES  (1, 'test', '测试环境', '用于开发者和测试人员进行单元测试、集成测试等，以确保代码的功能正确无误', now(), now()),
        (2, 'stage', '预发布环境', '预发布或模拟生产环境，用于进行用户验收测试(UAT)和最终的系统检查', now(), now()),
        (3, 'prod', '生产环境', '应用程序实际运行并面向外部用户的环境', now(), now());

INSERT INTO `xxl_conf_application` (id, appname, name, `desc`, add_time, update_time)
VALUES (1, 'xxl-conf-sample', '示例应用', '示例应用，演示使用', '2025-01-18 20:03:13', '2025-01-18 20:03:13');

INSERT INTO `xxl_conf_data` (id, env, appname, `key`, value, `desc`, add_time, update_time)
VALUES (1, 'test', 'xxl-conf-sample', 'sample.key01', '100', '演示配置01', '2025-01-18 20:04:05', '2025-01-18 20:04:05'),
       (2, 'test', 'xxl-conf-sample', 'sample.key02', '200', '演示配置02', '2025-01-18 20:05:14', '2025-01-18 20:08:42'),
       (3, 'test', 'xxl-conf-sample', 'sample.key03', '300', '演示配置03', '2025-01-18 20:12:06', '2025-01-18 20:12:06');

commit;
