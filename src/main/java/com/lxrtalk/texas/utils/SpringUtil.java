package com.lxrtalk.texas.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * @author lxr
 * 2015年8月4日 上午11:27:33
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    static ApplicationContext context ;

	@Autowired
    public static void setContext(ApplicationContext appcontext) {
        context = appcontext;
    }
    public void setApplicationContext(ApplicationContext appcontext) throws BeansException {
        context = appcontext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    
    public static Object getBean(String beanName){
        return context.getBean(beanName);
    }
}