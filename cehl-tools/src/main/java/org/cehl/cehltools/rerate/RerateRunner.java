package org.cehl.cehltools.rerate;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


public class RerateRunner {
	
	int runYear = 2020;
	
    public static void main( String[] args )
    {

    	SpringApplicationBuilder appBuilder = new SpringApplicationBuilder()
    	        .sources(RerateConfig.class);
    	
    	ConfigurableApplicationContext context = appBuilder.run(args);

    	RerateRunner runner = new RerateRunner();
    	runner.run5(context);
    }
    
    void run(ApplicationContext context) {

    	RerateJob job =context.getBean(RerateJob.class);
    	
    	job.reratePlayers(runYear);
    
    }
    
    void run2(ApplicationContext context) {

    	CsvRerateJob job =context.getBean(CsvRerateJob.class);
    	
    	job.runJob(runYear);
    
    }
    
    void run3(ApplicationContext context) {

    	DrsRerateJob job =context.getBean(DrsRerateJob.class);
    	
    	job.runJob(runYear);
    
    }
    
    void run4(ApplicationContext context) {

    	RerateJob2 job =context.getBean(RerateJob2.class);
    	
    	job.runJob(runYear);
    
    }
    
    void run5(ApplicationContext context) {

    	RerateJob3 job =context.getBean(RerateJob3.class);
    	
    	job.runJob(runYear);
    
    }
}
