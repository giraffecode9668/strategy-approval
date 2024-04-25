package com.giraffe.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CommonSpringContextUtil implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CommonSpringContextUtil.applicationContext = applicationContext;
    }


    public static <T> T getBean(String name, Class<T> clz) {
        return applicationContext.getBean(name, clz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }
}
