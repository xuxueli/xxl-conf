package com.xxl.conf.core.core;

import com.xxl.conf.core.exception.XxlConfException;
import com.xxl.conf.core.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * xxl conf, mirror conf data in local file
 *
 * @author xuxueli 2018-06-11 21:38:18
 */
public class XxlConfMirrorConf {
    private static Logger logger = LoggerFactory.getLogger(XxlConfMirrorConf.class);

    private static String mirrorfile = null;

    public static void init(String mirrorfileParam){
        // valid
        if (mirrorfileParam==null || mirrorfileParam.trim().length()==0) {
            throw new XxlConfException("xxl-conf mirrorfileParam can not be empty");
        }

        mirrorfile = mirrorfileParam;
    }

    /**
     * read mirror conf
     *
     * @return
     */
    public static Map<String, String> readConfMirror(){
        Properties mirrorProp = PropUtil.loadFileProp( mirrorfile );
        if (mirrorProp!=null && mirrorProp.stringPropertyNames()!=null && mirrorProp.stringPropertyNames().size()>0) {
            Map<String, String> mirrorConfData = new HashMap<>();
            for (String key: mirrorProp.stringPropertyNames()) {
                mirrorConfData.put(key, mirrorProp.getProperty(key));
            }
            return mirrorConfData;
        }
        return null;
    }

    /**
     * write mirror conf
     *
     * @param mirrorConfDataParam
     */
    public static void writeConfMirror(Map<String, String> mirrorConfDataParam){
        Properties properties = new Properties();
        for (Map.Entry<String, String> confItem: mirrorConfDataParam.entrySet()) {
            properties.setProperty(confItem.getKey(), confItem.getValue());
        }

        // write mirror file
        PropUtil.writeFileProp(properties, mirrorfile);
    }

}
