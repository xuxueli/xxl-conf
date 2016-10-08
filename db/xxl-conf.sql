
CREATE TABLE `XXL_CONF_GROUP` (
  `group_name` varchar(100) NOT NULL,
  `group_title` varchar(100) NOT NULL COMMENT '描述',
  PRIMARY KEY (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `XXL_CONF_GROUP` VALUES ('default', '默认分组');

CREATE TABLE `XXL_CONF_NODE` (
  `node_group` varchar(100) NOT NULL COMMENT '分组',
  `node_key` varchar(100) NOT NULL COMMENT '配置Key',
  `node_value` varchar(512) DEFAULT NULL COMMENT '配置Value',
  `node_desc` varchar(100) DEFAULT NULL COMMENT '配置简介',
  UNIQUE KEY `u_group_key` (`node_group`,`node_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `XXL_CONF_NODE` VALUES ('default', 'key01', '168', '测试配置01'), ('default', 'key02', '127.0.0.1:3307', '测试配置02');

COMMIT;

