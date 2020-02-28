package org.cehl.cehltools.rerate;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

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

}
