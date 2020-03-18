package org.cehl.cehltools.config;

import java.io.File;

import org.cehl.commons.spring.StaticContextAccessor;
import org.cehl.commons.spring.StaticPropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration(exclude={JpaRepositoriesAutoConfiguration.class,
		  DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = { "org.cehl.cehltools.jobs" })
public class CehlToolsConfig {
	
	@Autowired
	Environment env;
	
	@Bean
	public StaticContextAccessor staticContextAccessor(){
		return new StaticContextAccessor();
	}
	
	@Bean
	public StaticPropertyAccessor staticPropertyAccessor(){
		return new StaticPropertyAccessor();
	}
	
	@Bean
    public File simLocation(){
		return new File(env.getRequiredProperty("sim.location"));
    }
    
	@Bean
    public File backupLocation(){
		return new File(env.getRequiredProperty("backup.location"));
    }
	
	@Bean
    public File baseInputLocation(){
		return new File(env.getRequiredProperty("input.base.location"));
    }
	
	@Bean
    public File baseOutputLocation(){
		return new File(env.getRequiredProperty("output.base.location"));
    }
    
	@Bean
    public String leaguePrefix(@Qualifier("isPlayoffMode") boolean playoffMode){
		String prefix = env.getRequiredProperty("league.file.prefix") + (playoffMode ? "PLF" : "");
		
		return prefix;
    }
    
	@Bean
    public boolean isPlayoffMode(){
    	return env.getRequiredProperty("league.playoff.mode", Boolean.class);
    }
	
}
