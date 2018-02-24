package com.xxl.conf.core.core;

import com.xxl.conf.core.env.Environment;
import com.xxl.conf.core.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * local prop conf
 *
 * @author xuxueli 2018-02-01 19:25:52
 */
public class XxlConfPropConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfPropConf.class);

    private static final ConcurrentHashMap<String, String> propConf = new ConcurrentHashMap<>();
    private static void init(){
        // local prop
        if (Environment.LOCAL_PROP!=null && Environment.LOCAL_PROP.trim().length()>0) {
            Properties localProp = PropUtil.loadProp(Environment.LOCAL_PROP);
            if (localProp!=null && localProp.stringPropertyNames()!=null && localProp.stringPropertyNames().size()>0) {
                for (String key: localProp.stringPropertyNames()) {
                    propConf.put(key, localProp.getProperty(key));
                }
            }
        }

        logger.info(">>>>>>>>>> xxl-conf, XxlConfPropConf init success.");
    }
    static {
        init();
    }


    /**
     * get conf from local prop
     *
     * @param key
     * @return
     */
    public static String get(String key){
        return propConf.get(key);
    }

}
