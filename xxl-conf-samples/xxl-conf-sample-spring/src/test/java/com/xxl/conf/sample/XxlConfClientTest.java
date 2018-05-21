package com.xxl.conf.sample;

import com.xxl.conf.core.XxlConfClient;
import com.xxl.conf.core.core.XxlConfLocalCacheConf;
import com.xxl.conf.core.core.XxlConfZkConf;
import com.xxl.conf.core.util.PropUtil;
import org.junit.Test;

import java.util.Properties;

/**
 * client test
 *
 * @author xuxueli 2018-02-01 20:02:56
 */
public class XxlConfClientTest {

    @Test
    public void clientTest() {

        String zkaddress = null;
        String zkpath = null;
        String zkdigest = null;

        Properties envPropFile = PropUtil.loadProp("xxl-conf.properties");

        if (envPropFile!=null && envPropFile.stringPropertyNames()!=null && envPropFile.stringPropertyNames().size()>0) {
            for (String key: envPropFile.stringPropertyNames()) {
                if ("xxl.conf.zkaddress".equals(key)) {
                    zkaddress = envPropFile.getProperty(key);	// replace if envprop not exist
                } else if ("xxl.conf.zkpath".equals(key)) {
                    zkpath = envPropFile.getProperty(key);
                } else if ("xxl.conf.zkdigest".equals(key)) {
                    zkdigest = envPropFile.getProperty(key);
                }
            }
        }


        XxlConfZkConf.init(zkaddress, zkpath, zkdigest);									// init zk client
        XxlConfLocalCacheConf.init();




        String value = XxlConfClient.get("default.key01", null);
        System.out.println(value);
    }

}
