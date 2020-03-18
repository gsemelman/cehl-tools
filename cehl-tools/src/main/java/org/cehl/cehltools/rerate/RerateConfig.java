package org.cehl.cehltools.rerate;

import java.io.File;

import javax.sql.DataSource;

import org.cehl.commons.spring.StaticContextAccessor;
import org.cehl.commons.spring.StaticPropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:rerate.application.properties")
@Import(PersistenceJPAConfig.class)
public class RerateConfig {

	@Bean
	@Primary
	public DataSource dataSource() 
	{
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.url("jdbc:mysql://localhost/player_ratings");
		dataSourceBuilder.username("root");
		dataSourceBuilder.password("root");
		//dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
		return dataSourceBuilder.build();
	}
	
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

	@Bean
	public RerateJob rerateJob() {
		return new RerateJob();
	}

}
