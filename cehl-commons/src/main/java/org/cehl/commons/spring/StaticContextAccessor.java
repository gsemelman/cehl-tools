package org.cehl.commons.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StaticContextAccessor {
    private static StaticContextAccessor instance;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }
    
    public static Object getBean(String name) {
    	return getApplicationContext().getBean(name); 
    }
    
    public static <T> T getBean(Class<T> type) {
    	return getApplicationContext().getBean(type); 
    }
    
    public static <T> T getBean(String beanName, Class<T> requiredType) {
    	return getApplicationContext().getBean(beanName, requiredType); 
    }
    
    public static Object getBean(String beanName, Object... args) {
    	return getApplicationContext().getBean(beanName, args);
    }
    
    public static <T> T getBean(Class<T> requiredType, Object... args) {
    	return getApplicationContext().getBean(requiredType, args);
    }
    
    public static ApplicationContext getApplicationContext() {
    	if(instance == null){
    		throw new RuntimeException("StaticContextAccessor instance is null and has not been registered. "
    				+ "ApplicationContext must be initialized first");
    	}
    	
    	return instance.applicationContext;
    }
    
    public static void setApplicationContext(ApplicationContext applicationContext){
    	instance.applicationContext = applicationContext;
    }
    
}
