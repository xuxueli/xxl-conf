package com.xxl.conf.core.listener.impl;

import com.xxl.conf.core.listener.XxlConfListener;
import com.xxl.conf.core.listener.XxlConfListenerFactory;
import com.xxl.conf.core.spring.XxlConfFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xxl conf annotaltion refresh
 *
 * @author xuxueli 2018-02-204 01:46:20
 */
public class AnnoRefreshXxlConfListener implements XxlConfListener {


    // ---------------------- listener ----------------------

    // bean prop: object + field
    public static class BeanField{
        private Object object;
        private Field field;

        public BeanField() {
        }

        public BeanField(Object object, Field field) {
            this.object = object;
            this.field = field;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }
    }

    // listener(key):beanprop(object-field) = 1:N
    private List<BeanField> beanFieldList = Collections.synchronizedList(new ArrayList<BeanField>());
    public void addObject(BeanField beanProp){
        for (BeanField item: beanFieldList) {
            if (item.getObject() == beanProp.getObject() && item.getField()==beanProp.getField()) {
                return;
            }
        }
        beanFieldList.add(beanProp);
    }


    // ---------------------- listener map ----------------------

    /**
     *   key:listener = 1:1
     */
    private static ConcurrentHashMap<String, AnnoRefreshXxlConfListener> keyListener = new ConcurrentHashMap<>();

    /**
     * get listener this key
     * @param key
     * @return
     */
    public static AnnoRefreshXxlConfListener getAnnoRefreshXxlConfListener(String key){
        AnnoRefreshXxlConfListener annoRefreshXxlConfListener = keyListener.get(key);
        if (annoRefreshXxlConfListener == null) {
            annoRefreshXxlConfListener = new AnnoRefreshXxlConfListener();
            XxlConfListenerFactory.addListener(key, annoRefreshXxlConfListener);    // add listener, just once when first time
        }
        keyListener.put(key, annoRefreshXxlConfListener);
        return annoRefreshXxlConfListener;
    }
    public static void addKeyObject(String key, Object object, Field field){
        AnnoRefreshXxlConfListener annoRefreshXxlConfListener = AnnoRefreshXxlConfListener.getAnnoRefreshXxlConfListener(key);
        annoRefreshXxlConfListener.addObject(new BeanField(object, field));
    }

    @Override
    public void onChange(String key) throws Exception {
        if (beanFieldList!=null && beanFieldList.size()>0) {
            for (BeanField beanField: beanFieldList) {
                XxlConfFactory.refreshBeanWithXxlConf(beanField.getObject(), Arrays.asList(beanField.getField()));
            }
        }
    }
}
