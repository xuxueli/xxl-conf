package com.xxl.conf.core.core;

import com.xxl.conf.core.XxlConfClient;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * local cache conf
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfLocalCacheConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfClient.class);

    private static CacheManager cacheManager = null;
    private static Cache<String, String> xxlConfLocalCache = null;
    static {
        // cacheManager
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);		// default use ehcche.xml under src

        // xxlConfLocalCache
        xxlConfLocalCache = cacheManager.createCache("xxlConfLocalCache",
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(1000))	// .withExpiry、.withEvictionAdvisor （default lru）
        );

        logger.info(">>>>>>>>>> xxl-conf, local cache init success.");
    }

    /**
     * close cache manager
     */
    public static void close(){
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    /**
     * set conf
     *
     * @param key
     * @param value
     * @return
     */
    public static void set(String key, String value) {
        xxlConfLocalCache.put(key, value);
        logger.info(">>>>>>>>>> xxl-conf: SET: [{}={}]", key, value);


    }

    /**
     * remove conf
     *
     * @param key
     * @return
     */
    public static void remove(String key) {
        if (xxlConfLocalCache!=null && xxlConfLocalCache.containsKey(key)) {
            xxlConfLocalCache.remove(key);
        }
        logger.info(">>>>>>>>>> xxl-conf: REMOVE: [{}]", key);
    }

    /**
     * get conf
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        if (xxlConfLocalCache!=null && xxlConfLocalCache.containsKey(key)) {
            return xxlConfLocalCache.get(key);
        }
        return null;
    }

}
