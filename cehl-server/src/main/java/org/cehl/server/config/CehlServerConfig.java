package org.cehl.server.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class CehlServerConfig {

	@Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
    	return new PropertySourcesPlaceholderConfigurer();
    }
	
	
}
