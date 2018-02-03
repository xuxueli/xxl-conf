package com.xxl.conf.core.core;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * local cache conf
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfLocalCacheConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfClient.class);

    private static CacheManager cacheManager = null;
    private static Cache<String, CacheNode> xxlConfLocalCache = null;
    static {
        // cacheManager
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);		// default use ehcche.xml under src

        // xxlConfLocalCache
        xxlConfLocalCache = cacheManager.createCache("xxlConfLocalCache",
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, CacheNode.class, ResourcePoolsBuilder.heap(1000))	// .withExpiry、.withEvictionAdvisor （default lru）
        );

        logger.info(">>>>>>>>>> xxl-conf, XxlConfLocalCacheConf init success.");
    }

    /**
     * close cache manager
     */
    public static void destroy(){
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    /**
     * refresh conf (watch + refresh)
     */
    public static void reloadAll(){
        Set<String> keySet = new HashSet<>();
        Iterator<Cache.Entry<String, CacheNode>> iterator = xxlConfLocalCache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<String, CacheNode> item = iterator.next();
            keySet.add(item.getKey());
        }
        if (keySet.size() > 1) {
            for (String key: keySet) {
                String zkData = XxlConfZkClient.getPathDataByKey(key);
                xxlConfLocalCache.put(key, new CacheNode(zkData));
            }
        }
    }

    /**
     * set conf (invoke listener)
     *
     * @param key
     * @param value
     * @return
     */
    public static void set(String key, String value) {
        xxlConfLocalCache.put(key, new CacheNode(value));
        logger.info(">>>>>>>>>> xxl-conf: SET: [{}={}]", key, value);

        XxlConfListenerFactory.onChange(key);
    }

    /**
     * update conf  (only update exists key)  (invoke listener)
     *
     * @param key
     * @param value
     */
    public static void update(String key, String value) {
        if (xxlConfLocalCache!=null && xxlConfLocalCache.containsKey(key)) {
            xxlConfLocalCache.put(key, new CacheNode(value));
            logger.info(">>>>>>>>>> xxl-conf: UPDATE: [{}={}]", key, value);

            XxlConfListenerFactory.onChange(key);
        }
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
    public static CacheNode get(String key) {
        if (xxlConfLocalCache!=null && xxlConfLocalCache.containsKey(key)) {
            CacheNode cacheNode = xxlConfLocalCache.get(key);
            return cacheNode;
        }
        return null;
    }


    /**
     * local cache node
     */
    public static class CacheNode{
        private String value;

        public CacheNode() {
        }

        public CacheNode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
