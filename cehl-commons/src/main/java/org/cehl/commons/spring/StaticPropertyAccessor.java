package org.cehl.commons.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyAccessor {
    private static StaticPropertyAccessor instance;
    
    @Autowired
    private Environment env;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }
    
    public static String getProperty(String key){
    	return getEnv().getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue){
    	return getEnv().getProperty(key, defaultValue);
    }
    
    public static <T> T getProperty(String key, Class<T> targetType){
    	return getEnv().getProperty(key, targetType);
    }
    
    public static String getRequiredProperty(String key){
    	return getEnv().getRequiredProperty(key);
    }
    
    public static <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException{
		return getEnv().getRequiredProperty(key, targetType);
    }
    
    public static Environment getEnv(){
    	if(instance == null){
    		throw new RuntimeException("StaticPropertyAccessor instance is null and has not been registered. "
    				+ "ApplicationContext must be initialized first");
    	}
    	
    	return instance.env;
    }
    
    public static void setEnv(Environment env){
    	instance.env = env;
    }
    
    
}
