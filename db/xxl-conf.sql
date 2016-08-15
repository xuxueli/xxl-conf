
CREATE TABLE `znode_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `znode_key` varchar(100) NOT NULL,
  `znode_value` varchar(512) DEFAULT NULL,
  `znode_desc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


