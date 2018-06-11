//package com.xxl.conf.admin.core.config;
//
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.PoolProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * datasource config
// *
// * @author xuxueli 2018-06-08 21:48:19
// */
//@Configuration
//public class DatasourceConfig {
//
//    @Bean(name = "dataSource")
//    public DataSource dataSource() {
//
//        PoolProperties poolProperties = new PoolProperties();
//        poolProperties.setUrl("jdbc:mysql://localhost:3306/xx");
//        poolProperties.setDriverClassName("com.mysql.jdbc.Driver");
//        poolProperties.setUsername("root");
//        poolProperties.setPassword("root_pwd");
//        poolProperties.setJmxEnabled(true);
//        poolProperties.setTestWhileIdle(false);
//        poolProperties.setTestOnBorrow(true);
//        poolProperties.setValidationQuery("SELECT 1");
//        poolProperties.setTestOnReturn(false);
//        poolProperties.setValidationInterval(30000);
//        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
//        poolProperties.setMaxActive(100);
//        poolProperties.setInitialSize(10);
//        poolProperties.setMaxWait(10000);
//        poolProperties.setRemoveAbandonedTimeout(60);
//        poolProperties.setMinEvictableIdleTimeMillis(30000);
//        poolProperties.setMinIdle(10);
//        poolProperties.setLogAbandoned(true);
//        poolProperties.setRemoveAbandoned(true);
//
//        DataSource datasource = new DataSource();
//        datasource.setPoolProperties(poolProperties);
//
//        return datasource;
//    }
//
//}
