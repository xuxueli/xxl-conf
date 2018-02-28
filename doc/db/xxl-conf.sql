CREATE database if NOT EXISTS `xxl-conf` default character set utf8 collate utf8_general_ci;
use `xxl-conf`;


CREATE TABLE `xxl_conf_project` (
  `appname` varchar(100) NOT NULL COMMENT 'AppName',
  `title` varchar(100) NOT NULL COMMENT '项目名称',
  PRIMARY KEY (`appname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node` (
  `key` varchar(100) NOT NULL COMMENT '配置Key',
  `appname` varchar(100) NOT NULL COMMENT '所属项目AppName',
  `title` varchar(100) NOT NULL COMMENT '配置描述',
  `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `xxl_conf_project` VALUES ('default', '示例项目');
INSERT INTO `xxl_conf_node` VALUES ('default.key01', 'default', '测试配置01', '一111'), ('default.key02', 'default', '测试配置02', '二222'), ('default.key03', 'default', '测试配置03', '三333');


COMMIT;
