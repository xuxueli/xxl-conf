package com.xxl.conf.core.listener;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf listener
 *
 * @author xuxueli 2018-02-04 01:27:30
 */
public class XxlConfListenerRepository {
    private static final Logger logger = LoggerFactory.getLogger(XxlConfListenerRepository.class);


    // ---------------------- init ----------------------

    private final XxlConfBootstrap xxlConfBootstrap;
    public XxlConfListenerRepository(XxlConfBootstrap xxlConfBootstrap) {
        this.xxlConfBootstrap = xxlConfBootstrap;
    }

    // ---------------------- store ----------------------

    /**
     * listener repository
     *
     * <pre>
     *     // Data Structure
     *     {
     *          "app02##k1": [{
     *              ...
     *          }],
     *          "app02##k1": {
     *              ...
     *          }
     *      }
     * </pre>
     */
    private final ConcurrentHashMap<String, List<XxlConfListener>> keyListenerRepository = new ConcurrentHashMap<>();
    private final List<XxlConfListener> noKeyConfRepository = Collections.synchronizedList(new ArrayList<>());

    /**
     * build cache key
     */
    public static String buildListenerKey(String appname, String key){
        return String.format("%s##%s", appname, key);
    }

    /**
     * add none-key listener, nofity when first-use + change
     *
     * @param xxlConfListener  listener
     */
    public void addNoKeyListener(XxlConfListener xxlConfListener){
        noKeyConfRepository.add(xxlConfListener);
    }
    /**
     * add listener, nofity when first-use + change
     *
     * @param appname           appname
     * @param key               key
     * @param xxlConfListener   listener
     * @return  true: add success
     */
    public boolean addListener(String appname, String key, XxlConfListener xxlConfListener){
        // valid
        if (appname==null || appname.trim().isEmpty()) {
            appname = xxlConfBootstrap.getAppname();
        }
        if (key==null || key.trim().isEmpty()) {
            return false;
        }
        if (xxlConfListener == null) {
            return false;
        }

        // 1、first use
        try {
            String value = XxlConfHelper.get(appname, key, null);
            xxlConfListener.onChange(appname, key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 2、watch change
        String listenerKey = buildListenerKey(appname, key);

        List<XxlConfListener> listeners = keyListenerRepository.computeIfAbsent(listenerKey, k -> new ArrayList<>());
        listeners.add(xxlConfListener);

        return true;
    }

    /**
     * invoke listener on xxl conf change
     *
     * @param key key
     */
    public void notifyChange(String appname, String key, String value){
        // valid
        if (appname==null || appname.trim().isEmpty()) {
            appname = xxlConfBootstrap.getAppname();
        }
        if (key==null || key.trim().isEmpty()) {
            return;
        }

        // build listenerKey
        String listenerKey = buildListenerKey(appname, key);

        // 1、notify by key
        List<XxlConfListener> keyListeners = keyListenerRepository.get(listenerKey);
        if (keyListeners!=null && !keyListeners.isEmpty()) {
            for (XxlConfListener listener : keyListeners) {
                try {
                    listener.onChange(appname, key, value);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        // 1、notify none-key
        if (!noKeyConfRepository.isEmpty()) {
            for (XxlConfListener confListener: noKeyConfRepository) {
                try {
                    confListener.onChange(appname, key, value);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info(">>>>>>>>>>> xxl-conf, XxlConfListenerRepository notifyChange finish: appname={}, key={}", appname, key);
    }

}
