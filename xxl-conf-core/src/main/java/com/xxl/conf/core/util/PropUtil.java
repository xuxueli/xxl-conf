package com.xxl.conf.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * prop util
 *
 * @author xuxueli 2018-02-23 21:57:05
 */
public class PropUtil {
    private static Logger logger = LoggerFactory.getLogger(PropUtil.class);

    /**
     * load prop
     *
     * @param propertyFileName disk path when start with "file:", other classpath
     * @return
     */
    public static Properties loadProp(String propertyFileName) {
        Properties prop = new Properties();
        InputStream in = null;
        try {

            // load file location, disk or resource
            if (propertyFileName.startsWith("file:")) {
                URL url = new File(propertyFileName.substring("file:".length())).toURI().toURL();
                in = new FileInputStream(url.getPath());
            } else {
                ClassLoader loder = Thread.currentThread().getContextClassLoader();
                /*URL url = loder.getResource(propertyFileName);
                in = new FileInputStream(url.getPath());*/
                in = loder.getResourceAsStream(propertyFileName);
            }
            if (in != null) {
                //prop.load(in);
                prop.load(new InputStreamReader(in, "utf-8"));
            }
        } catch (IOException e) {
            logger.error(">>>>>>>>>> xxl-conf, PropUtil load prop fail [{}]", propertyFileName);
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
     * write prop to disk
     *
     * @param properties
     * @param filePathName
     * @return
     */
    public static boolean writeProp(Properties properties, String filePathName){
        FileOutputStream fileOutputStream = null;
        try {
            //properties.store(new FileWriter(filePathName), null);

            fileOutputStream = new FileOutputStream(filePathName, false);
            properties.store(new OutputStreamWriter(fileOutputStream, "utf-8"), null);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
