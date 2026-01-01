package com.xxl.conf.core.listener;

import com.xxl.conf.core.XxlConfHelper;
import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import com.xxl.conf.core.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.tool.concurrent.MessageQueue;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
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
public class XxlConfListenerHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlConfListenerHelper.class);


    // ---------------------- init ----------------------

    private final XxlConfBootstrap xxlConfBootstrap;
    public XxlConfListenerHelper(XxlConfBootstrap xxlConfBootstrap) {
        this.xxlConfBootstrap = xxlConfBootstrap;
    }

    // ---------------------- start / stop ----------------------

    private volatile MessageQueue<ConfDataCacheDTO> notifyQuene;

    /**
     * start
     */
    public void start(){
        notifyQuene = new MessageQueue<>(
                "produceMessageQueue",
                messages -> {

                    // process message
                    for (ConfDataCacheDTO confDataCacheDTO: messages) {
                        // valid
                        if (StringTool.isBlank(confDataCacheDTO.getAppname())) {
                            confDataCacheDTO.setAppname(xxlConfBootstrap.getAppname());
                        }
                        if (StringTool.isBlank(confDataCacheDTO.getKey())) {
                            return;
                        }

                        // build listenerKey
                        String listenerKey = buildListenerKey(confDataCacheDTO.getAppname(), confDataCacheDTO.getKey());

                        // 1、notify by key
                        List<XxlConfListener> keyListeners = keyListenerRepository.get(listenerKey);
                        if (CollectionTool.isNotEmpty(keyListeners)) {
                            for (XxlConfListener listener : keyListeners) {
                                try {
                                    listener.onChange(confDataCacheDTO.getAppname(), confDataCacheDTO.getKey(), confDataCacheDTO.getValue());
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }

                        // 1、notify none-key
                        if (!noKeyConfRepository.isEmpty()) {
                            for (XxlConfListener confListener: noKeyConfRepository) {
                                try {
                                    confListener.onChange(confDataCacheDTO.getAppname(), confDataCacheDTO.getKey(), confDataCacheDTO.getValue());
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                        logger.info(">>>>>>>>>>> xxl-conf, XxlConfListenerHelper notifyChange finish: appname={}, key={}", confDataCacheDTO.getAppname(), confDataCacheDTO.getKey());
                    }
                },
                1,
                1);
    }

    /**
     * stop
     */
    public void stop(){
        if (notifyQuene != null) {
            notifyQuene.stop();
        }
    }

    // ---------------------- store ----------------------

    /**
     * build ListenerKey
     */
    public static String buildListenerKey(String appname, String key){
        return String.format("%s##%s", appname, key);
    }

    /**
     * listener repository
     *
     * <pre>
     *     {
     *          "app02##k1": [{             // ListenerKey
     *              XxlConfListener01,
     *              XxlConfListener02
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

    // ---------------------- tool ----------------------

    /**
     * notifyChange for listener
     *
     * @param confDataCacheDTO confDataCacheDTO
     */
    public void notifyChange(ConfDataCacheDTO confDataCacheDTO){
        notifyQuene.produce(confDataCacheDTO);
    }

}
