/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.manager.factory;

/**
 * 
 * @author xieruidong 2013年11月6日 下午3:34:25
 */
import java.lang.reflect.Constructor;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class StageManagerFactory implements ApplicationContextAware {

    private static ApplicationContext            context = null;
    
    @SuppressWarnings("rawtypes")
	private static Map<Long, Map<Class, Object>> cache   = new MapMaker().makeComputingMap(new Function<Long, Map<Class, Object>>() {

                                                             public Map<Class, Object> apply(final Long pumpId) {
                                                                 return new MapMaker().makeComputingMap(new Function<Class, Object>() {

                                                                     public Object apply(Class instanceClass) {
                                                                         return newInstance(pumpId, instanceClass);
                                                                     }
                                                                 });
                                                             }
                                                         });

    @SuppressWarnings("rawtypes")
	private static Object newInstance(Long pumpId, Class instanceClass) {
        Object obj = newInstance(instanceClass, pumpId);// 通过反射调用构造函数进行初始化
        autowire(obj);
        return obj;
    }

    @SuppressWarnings("unchecked")
	public static <T> T getInstance(Long pumpId, Class<T> instanceClass) {

    	T ret = (T) cache.get(pumpId).get(instanceClass);
        return ret;
    }
    
    public static void destroy(Long pumpId) {
    	cache.remove(pumpId);
    }

    public static void autowire(Object obj) {
        context.getAutowireCapableBeanFactory().autowireBeanProperties(obj,
                                                                       AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
                                                                       true);
    }




    // ==================== helper method =======================

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object newInstance(Class type, Long pumpId) {
        Constructor _constructor = null;
        Object[] _constructorArgs = new Object[1];
        _constructorArgs[0] = pumpId;

        try {
            _constructor = type.getConstructor(new Class[] { Long.class });
        } catch (NoSuchMethodException e) {
        	return null;
        }

        try {
            return _constructor.newInstance(_constructorArgs);
        } catch (Exception e) {
        	return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}

