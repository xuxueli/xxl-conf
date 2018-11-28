CREATE database if NOT EXISTS `xxl-conf` default character set utf8 collate utf8_general_ci;
use `xxl-conf`;


CREATE TABLE `xxl_conf_env` (
  `env` varchar(100) NOT NULL COMMENT 'Env',
  `title` varchar(100) NOT NULL COMMENT '环境名称',
  `order` tinyint(4) NOT NULL DEFAULT '0' COMMENT '显示排序',
  PRIMARY KEY (`env`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_project` (
  `appname` varchar(100) NOT NULL COMMENT 'AppName',
  `title` varchar(100) NOT NULL COMMENT '项目名称',
  PRIMARY KEY (`appname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_user` (
  `username` varchar(100) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `permission` tinyint(4) NOT NULL DEFAULT '0' COMMENT '权限：0-普通用户、1-管理员',
  `permission_data` varchar(1000) DEFAULT NULL COMMENT '权限配置数据',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node` (
  `env` varchar(100) NOT NULL COMMENT 'Env',
  `key` varchar(200) NOT NULL COMMENT '配置Key',
  `appname` varchar(100) NOT NULL COMMENT '所属项目AppName',
  `title` varchar(100) NOT NULL COMMENT '配置描述',
  `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
  PRIMARY KEY (`env`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node_log` (
  `env` varchar(255) NOT NULL COMMENT 'Env',
  `key` varchar(200) NOT NULL COMMENT '配置Key',
  `title` varchar(100) NOT NULL COMMENT '配置描述',
  `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
  `addtime` datetime NOT NULL COMMENT '操作时间',
  `optuser` varchar(100) NOT NULL COMMENT '操作人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `addtime` datetime NOT NULL,
  `env` varchar(100) NOT NULL COMMENT 'Env',
  `key` varchar(200) NOT NULL COMMENT '配置Key',
  `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `xxl_conf_env` VALUES ('test', '测试环境', 1), ('ppe', '预发布环境', 2), ('product', '生产环境', 3);
INSERT INTO `xxl_conf_project` VALUES ('default', '示例项目');
INSERT INTO `xxl_conf_user` VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL), ('user', 'e10adc3949ba59abbe56e057f20f883e', 0, 'default#test,default#ppe');
INSERT INTO `xxl_conf_node` VALUES ('test', 'default.key01', 'default', '测试配置01', '1'), ('test', 'default.key02', 'default', '测试配置02', '2'), ('test', 'default.key03', 'default', '测试配置03', '3');


COMMIT;
