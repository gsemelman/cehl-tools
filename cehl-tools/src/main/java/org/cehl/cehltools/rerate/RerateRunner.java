package org.cehl.cehltools.rerate;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


public class RerateRunner {
    public static void main( String[] args )
    {

    	SpringApplicationBuilder appBuilder = new SpringApplicationBuilder()
    	        .sources(RerateConfig.class);
    	
    	ConfigurableApplicationContext context = appBuilder.run(args);

    	RerateRunner runner = new RerateRunner();
    	runner.run2(context);
    }
    
    void run(ApplicationContext context) {

    	RerateJob job =context.getBean(RerateJob.class);
    	
    	job.reratePlayers(2018);
    
    }
    
    void run2(ApplicationContext context) {

    	CsvRerateJob job =context.getBean(CsvRerateJob.class);
    	
    	job.runJob(2018);
    
    }
    
    void run3(ApplicationContext context) {

    	DrsRerateJob job =context.getBean(DrsRerateJob.class);
    	
    	job.runJob(2018);
    
    }
}
