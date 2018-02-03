package com.xxl.conf.core.listener;

import com.xxl.conf.core.spring.XxlConfFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf listener
 *
 * @author xuxueli 2018-02-04 01:27:30
 */
public class XxlConfListenerFactory {
    private static Logger logger = LoggerFactory.getLogger(XxlConfListenerFactory.class);

    /**
     * xxl conf listener repository
     */
    private static ConcurrentHashMap<String, List<XxlConfListener>> xxlConfListenerRepository = new ConcurrentHashMap<>();

    /**
     * add listener with xxl conf change
     *
     * @param key
     * @param xxlConfListener
     * @return
     */
    public static boolean addListener(String key, XxlConfListener xxlConfListener){
        if (key==null || key.trim().length()==0 || xxlConfListener==null) {
            return false;
        }
        List<XxlConfListener> listeners = xxlConfListenerRepository.get(key);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(xxlConfListener);
        xxlConfListenerRepository.put(key, listeners);
        return true;
    }

    /**
     * invoke listener on xxl conf change
     *
     * @param key
     */
    public static void onChange(String key){
        if (key==null || key.trim().length()==0) {
            return;
        }
        List<XxlConfListener> listeners = xxlConfListenerRepository.get(key);
        if (listeners==null || listeners.size()<1) {
            return;
        }
        for (XxlConfListener listener:listeners) {
            try {
                listener.onChange(key);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
