package com.xxl.conf.core.core;

import com.xxl.conf.core.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf, mirror conf data in local file
 *
 * @author xuxueli 2018-06-11 21:38:18
 */
public class XxlConfMirrorConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfMirrorConf.class);

    private static String mirrorfile = null;
    private static ConcurrentHashMap<String, String> mirrorConfData = null;

    public static void init(String mirrorfileParam){

        // open mirror
        mirrorfile = mirrorfileParam;
        if (!isOpend()) {
            logger.info(">>>>>>>>>> xxl-conf, XxlConfMirrorConf not open.");
            return;
        }

        // load mirror data
        mirrorConfData = new ConcurrentHashMap<>();
        Properties mirrorProp = PropUtil.loadProp( "file:" + mirrorfile );
        if (mirrorProp!=null && mirrorProp.stringPropertyNames()!=null && mirrorProp.stringPropertyNames().size()>0) {
            for (String key: mirrorProp.stringPropertyNames()) {
                mirrorConfData.put(key, mirrorProp.getProperty(key));
            }
        }

        logger.info(">>>>>>>>>> xxl-conf, XxlConfMirrorConf init success. [mirrorfile={}]", mirrorfile);
    }

    public static boolean isOpend() {
        return mirrorfile!=null && mirrorfile.trim().length()>0;
    }

    public static void writeConfMirror(Map<String, String> mirrorConfDataParam){
        if (!isOpend()) {
            return;
        }

        Properties properties = new Properties();
        for (Map.Entry<String, String> confItem: mirrorConfDataParam.entrySet()) {
            properties.setProperty(confItem.getKey(), confItem.getValue());
        }


        // write mirror file
        PropUtil.writeProp(properties, mirrorfile);

        // refresh mirror data
        mirrorConfData.clear();
        mirrorConfData.putAll(mirrorConfDataParam);

        logger.info(">>>>>>>>>> xxl-conf, write mirror conf success.");
    }

    public static String get(String key){
        if (!isOpend()) {
            return null;
        }

        return mirrorConfData.get(key);
    }

}
