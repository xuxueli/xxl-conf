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

        // preload: mirror or remote
        Map<String, String> preConfData = new HashMap<>();

        Map<String, String> mirrorConfData = XxlConfMirrorConf.readConfMirror();

        Map<String, String> remoteConfData = null;
        if (mirrorConfData!=null && mirrorConfData.size()>0) {
            remoteConfData = XxlConfRemoteConf.find(mirrorConfData.keySet());
        }

        if (mirrorConfData!=null && mirrorConfData.size()>0) {
            preConfData.putAll(mirrorConfData);
        }
        if (remoteConfData!=null && remoteConfData.size()>0) {
            preConfData.putAll(remoteConfData);
        }
        if (preConfData!=null && preConfData.size()>0) {
            for (String preKey: preConfData.keySet()) {
                set(preKey, preConfData.get(preKey), SET_TYPE.PRELOAD );
            }
        }

        // refresh thread
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!refreshThreadStop) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        refreshCacheAndMirror();
                    } catch (Exception e) {
                        if (!refreshThreadStop && !(e instanceof InterruptedException)) {
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
     * refresh Cache And Mirror, with real-time minitor
     */
    private static void refreshCacheAndMirror(){

        if (localCacheRepository.size()==0) {
            return;
        }

        XxlConfRemoteConf.monitor(localCacheRepository.keySet());

        // refresh cache: remote > cache
        Set<String> keySet = localCacheRepository.keySet();
        if (keySet.size() > 0) {

            Map<String, String> remoteDataMap = XxlConfRemoteConf.find(keySet);
            if (remoteDataMap!=null && remoteDataMap.size()>0) {
                for (String remoteKey:remoteDataMap.keySet()) {
                    String remoteData = remoteDataMap.get(remoteKey);

                    CacheNode existNode = localCacheRepository.get(remoteKey);
                    if (existNode!=null && existNode.getValue()!=null && existNode.getValue().equals(remoteData)) {
                        logger.debug(">>>>>>>>>> xxl-conf: RELOAD unchange-pass [{}].", remoteKey);
                    } else {
                        set(remoteKey, remoteData, SET_TYPE.RELOAD );
                    }

                }
            }

        }

        // refresh mirror: cache > mirror
        Map<String, String> mirrorConfData = new HashMap<>();
        for (String key: keySet) {
            CacheNode existNode = localCacheRepository.get(key);
            mirrorConfData.put(key, existNode.getValue()!=null?existNode.getValue():"");
        }
        XxlConfMirrorConf.writeConfMirror(mirrorConfData);

        logger.info(">>>>>>>>>> xxl-conf, refreshCacheAndMirror success.");
    }


    // ---------------------- inner api ----------------------

    public enum SET_TYPE{
        SET,        // first use
        RELOAD,     // value updated
        PRELOAD     // pre hot
    }

    /**
     * set conf (invoke listener)
     *
     * @param key
     * @param value
     * @return
     */
    private static void set(String key, String value, SET_TYPE optType) {
        localCacheRepository.put(key, new CacheNode(value));
        logger.info(">>>>>>>>>> xxl-conf: {}: [{}={}]", optType, key, value);

        // value updated, invoke listener
        if (optType == SET_TYPE.RELOAD) {
            XxlConfListenerFactory.onChange(key, value);
        }

        // new conf, new monitor
        if (optType == SET_TYPE.SET) {
            refreshThread.interrupt();
        }
    }

    /**
     * get conf
     *
     * @param key
     * @return
     */
    private static CacheNode get(String key) {
        if (localCacheRepository.containsKey(key)) {
            CacheNode cacheNode = localCacheRepository.get(key);
            return cacheNode;
        }
        return null;
    }

    /**
     * update conf  (only update exists key)  (invoke listener)
     *
     * @param key
     * @param value
     */
    /*private static void update(String key, String value) {
        if (localCacheRepository.containsKey(key)) {
            set(key, value, SET_TYPE.UPDATE );
        }
    }*/

    /**
     * remove conf
     *
     * @param key
     * @return
     */
    /*private static void remove(String key) {
        if (localCacheRepository.containsKey(key)) {
            localCacheRepository.remove(key);
        }
        logger.info(">>>>>>>>>> xxl-conf: REMOVE: [{}]", key);
    }*/


    // ---------------------- api ----------------------

    /**
     * get conf
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public static String get(String key, String defaultVal) {

        // level 1: local cache
        XxlConfLocalCacheConf.CacheNode cacheNode = XxlConfLocalCacheConf.get(key);
        if (cacheNode != null) {
            return cacheNode.getValue();
        }

        // level 2	(get-and-watch, add-local-cache)
        String remoteData = null;
        try {
            remoteData = XxlConfRemoteConf.find(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        set(key, remoteData, SET_TYPE.SET );		// support cache null value
        if (remoteData != null) {
            return remoteData;
        }

        return defaultVal;
    }

}
