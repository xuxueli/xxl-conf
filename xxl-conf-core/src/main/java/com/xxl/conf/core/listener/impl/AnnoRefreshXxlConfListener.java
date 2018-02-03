package com.xxl.conf.core.listener.impl;

import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import com.xxl.conf.core.spring.XxlConfFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf annotaltion refresh
 *
 * @author xuxueli 2018-02-204 01:46:20
 */
public class AnnoRefreshXxlConfListener implements XxlConfListener {

    // listener:object = 1:N
    private List<Object> objectList = Collections.synchronizedList(new ArrayList<>());
    public void addObject(Object object){
        if (!objectList.contains(object)) {
            objectList.add(object);
        }
    }

    /**
     *   key:listener = 1:1
     */
    private static ConcurrentHashMap<String, AnnoRefreshXxlConfListener> keyListener = new ConcurrentHashMap<>();

    /**
     * get listener whis key
     * @param key
     * @return
     */
    public static AnnoRefreshXxlConfListener getAnnoRefreshXxlConfListener(String key){
        AnnoRefreshXxlConfListener annoRefreshXxlConfListener = keyListener.get(key);
        if (annoRefreshXxlConfListener == null) {
            annoRefreshXxlConfListener = new AnnoRefreshXxlConfListener();
            XxlConfListenerFactory.addListener(key, annoRefreshXxlConfListener);    // add listener, just once when first time
        }
        keyListener.put(key, annoRefreshXxlConfListener);
        return annoRefreshXxlConfListener;
    }
    public static void addKeyObject(String key, Object object){
        AnnoRefreshXxlConfListener annoRefreshXxlConfListener = AnnoRefreshXxlConfListener.getAnnoRefreshXxlConfListener(key);
        annoRefreshXxlConfListener.addObject(object);
    }

    @Override
    public void onChange(String key) throws Exception {
        if (objectList!=null && objectList.size()>0) {
            for (Object object: objectList) {
                XxlConfFactory.refreshBeanWithXxlConf(object);
            }
        }
    }
}
