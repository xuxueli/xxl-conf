package com.xxl.conf.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * prop util
 *
 * @author xuxueli 2018-02-23 21:57:05
 */
public class PropUtil {
    private static Logger logger = LoggerFactory.getLogger(PropUtil.class);

    public static Properties loadProp(String propertyFileName) {
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
            logger.error(">>>>>>>>>> xxl-conf, PropUtil load prop fail [{}], error msg:{}", propertyFileName, e.getMessage());
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

}
