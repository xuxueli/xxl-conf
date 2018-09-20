package com.xxl.conf.core.core;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * local cache conf
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfLocalCacheConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfClient.class);


    // ---------------------- init/destroy ----------------------

    private static ConcurrentHashMap<String, CacheNode> localCacheRepository = null;

    private static Thread refreshThread;
    private static boolean refreshThreadStop = false;
    public static void init(){

        localCacheRepository = new ConcurrentHashMap<String, CacheNode>();

        // refresh thread
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!refreshThreadStop) {
                    try {
                        TimeUnit.SECONDS.sleep(60);
                        reloadAll();
                        logger.info(">>>>>>>>>> xxl-conf, refresh thread reload all success.");
                    } catch (Exception e) {
                        if (!refreshThreadStop) {
                            logger.error(">>>>>>>>>> xxl-conf, refresh thread error.");
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>> xxl-conf, refresh thread stoped.");
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();

        logger.info(">>>>>>>>>> xxl-conf, XxlConfLocalCacheConf init success.");
    }

    public static void destroy(){
        if (refreshThread != null) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }

    /**
     * local cache node
     */
    public static class CacheNode implements Serializable{
        private static final long serialVersionUID = 42L;

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


    // ---------------------- util ----------------------

    /**
     * reload all conf (watch + refresh)
     */
    public static void reloadAll(){
        Set<String> keySet = localCacheRepository.keySet();
        if (keySet.size() > 0) {
            for (String key: keySet) {
                String zkData = XxlConfZkConf.get(key);

                CacheNode existNode = localCacheRepository.get(key);
                if (existNode!=null && existNode.getValue()!=null && existNode.getValue().equals(zkData)) {
                    logger.debug(">>>>>>>>>> xxl-conf: RELOAD unchange-pass [{}].", key);
                } else {
                    set(key, zkData, "RELOAD");
                }

            }

            // write mirror
            Map<String, String> mirrorConfData = new HashMap<>();
            for (String key: keySet) {
                CacheNode existNode = localCacheRepository.get(key);
                // collect mirror data
                mirrorConfData.put(key, existNode.getValue()!=null?existNode.getValue():"");
            }
            XxlConfMirrorConf.writeConfMirror(mirrorConfData);

        }

    }

    /**
     * set conf (invoke listener)
     *
     * @param key
     * @param value
     * @return
     */
    public static void set(String key, String value, String optType) {
        localCacheRepository.put(key, new CacheNode(value));
        logger.info(">>>>>>>>>> xxl-conf: {}: [{}={}]", optType, key, value);

        XxlConfListenerFactory.onChange(key, value);
    }

    /**
     * update conf  (only update exists key)  (invoke listener)
     *
     * @param key
     * @param value
     */
    public static void update(String key, String value) {
        if (localCacheRepository.containsKey(key)) {
            set(key, value, "UPDATE");
        }
    }

    /**
     * remove conf
     *
     * @param key
     * @return
     */
    public static void remove(String key) {
        if (localCacheRepository.containsKey(key)) {
            localCacheRepository.remove(key);
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
        if (localCacheRepository.containsKey(key)) {
            CacheNode cacheNode = localCacheRepository.get(key);
            return cacheNode;
        }
        return null;
    }

}
