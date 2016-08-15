

CREATE TABLE XXL_CONF_NODE (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_key` varchar(100) NOT NULL,
  `node_value` varchar(512) DEFAULT NULL,
  `node_desc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;