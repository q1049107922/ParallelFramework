package com.parallel.framework;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class ContextHolder implements ApplicationContextAware {

    private static ApplicationContext context = null;


    public static void setContext(ApplicationContext context) {
        ContextHolder.context = context;
    }

    private ContextHolder(){}
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
