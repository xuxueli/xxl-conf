
CREATE TABLE XXL_CONF_NODE (
  `node_key` varchar(100) NOT NULL,
  `node_value` varchar(512) DEFAULT NULL,
  `node_desc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`node_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
