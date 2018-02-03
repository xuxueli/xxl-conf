package com.xxl.conf.core.core;

import com.xxl.conf.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    static {
        // default prop
        Properties prop = loadProp(Environment.DEFAULT_PROP);
        if (prop.stringPropertyNames()!=null && prop.stringPropertyNames().size()>0) {

            // prop file
            String propFileLocation = prop.getProperty(Environment.PROP_FILE_LOCATION);
            if (propFileLocation!=null && propFileLocation.trim().length()>0) {
                prop = loadProp(propFileLocation);
            }

            for (String key: prop.stringPropertyNames()) {
                propConf.put(key, prop.getProperty(key));
            }
        }

        logger.info(">>>>>>>>>> xxl-conf, XxlConfPropConf init success.");
    }

    private static Properties loadProp(String propertyFileName) {
        Properties prop = new Properties();
        InputStream in = null;
        try {

            // load file location, disk or resource
            URL url = null;
            if (propertyFileName.startsWith("file:")) {
                url = new File(propertyFileName.substring("file:".length())).toURI().toURL();
            } else {
                ClassLoader loder = Thread.currentThread().getContextClassLoader();
                url = loder.getResource(propertyFileName);
            }

            if (url != null) {
                in = new FileInputStream(url.getPath());
                if (in != null) {
                    prop.load(in);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return prop;
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
