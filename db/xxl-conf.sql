
CREATE TABLE XXL_CONF_NODE (
  `node_group` varchar(100) NOT NULL COMMENT '分组',
  `node_key` varchar(100) NOT NULL COMMENT '配置Key',
  `node_value` varchar(512) DEFAULT NULL COMMENT '配置Value',
  `node_desc` varchar(100) DEFAULT NULL COMMENT '配置简介',
  UNIQUE KEY `u_group_key` (`node_group`,`node_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

