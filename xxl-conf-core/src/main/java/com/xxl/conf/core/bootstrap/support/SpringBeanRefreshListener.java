package com.xxl.conf.core.bootstrap.support;

import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf annotaltion refresh
 *
 * @author xuxueli 2018-02-204 01:46:20
 */
public class SpringBeanRefreshListener implements XxlConfListener {


    // ---------------------- bean-field data ----------------------

    // object + field
    public static class BeanField{
        private String beanName;
        private String fieldName;

        public BeanField() {
        }

        public BeanField(String beanName, String fieldName) {
            this.beanName = beanName;
            this.fieldName = fieldName;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }

    /**
     * bean-field repository
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
    private static Map<String, List<BeanField>> listenerKey2BeanField = new ConcurrentHashMap<String, List<BeanField>>();
    public static void addBeanField(String appname, String key, BeanField beanField){

        // build listenerKey
        String listenerKey = XxlConfListenerRepository.buildListenerKey(appname, key);
        List<BeanField> beanFieldList = listenerKey2BeanField.computeIfAbsent(listenerKey, k -> new ArrayList<>());

        // avoid repeat refresh
        for (BeanField item: beanFieldList) {
            if (item.getBeanName().equals(beanField.getBeanName())
                    && item.getFieldName().equals(beanField.getFieldName())) {
                return;
            }
        }

        // add data
        beanFieldList.add(beanField);
    }

    // ---------------------- onChange ----------------------

    @Override
    public void onChange(String appname, String key, String value) throws Exception {

        // build listenerKey
        String listenerKey = XxlConfListenerRepository.buildListenerKey(appname, key);
        List<BeanField> beanFieldList = listenerKey2BeanField.get(listenerKey);

        if (beanFieldList!=null && !beanFieldList.isEmpty()) {
            for (BeanField beanField: beanFieldList) {
                SpringXxlConfBootstrap.refreshBeanField(null, beanField, value);
            }
        }
    }
}
