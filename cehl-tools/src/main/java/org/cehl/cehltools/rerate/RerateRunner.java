package org.cehl.cehltools.rerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.math3.util.Precision;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerRepository;
import org.cehl.cehltools.rerate.processor.DfRatingProcessor;
import org.cehl.cehltools.rerate.processor.DiRatingProcessor;
import org.cehl.cehltools.rerate.processor.DuRatingProcessor;
import org.cehl.cehltools.rerate.processor.EnRatingProcessor;
import org.cehl.cehltools.rerate.processor.ExRatingProcessor;
import org.cehl.cehltools.rerate.processor.ItRatingProcessor;
import org.cehl.cehltools.rerate.processor.PaRatingProcessor;
import org.cehl.cehltools.rerate.processor.PcRatingProcessor;
import org.cehl.cehltools.rerate.processor.ScRatingProcessor;
import org.cehl.cehltools.rerate.processor.StRatingProcessor;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;


public class RerateRunner {
    public static void main( String[] args )
    {

    	SpringApplicationBuilder appBuilder = new SpringApplicationBuilder()
    	        .sources(RerateConfig.class);
    	
    	ConfigurableApplicationContext context = appBuilder.run(args);

    	RerateRunner runner = new RerateRunner();
    	runner.run(context);
    }
    
    void run(ApplicationContext context) {

    	RerateJob job =context.getBean(RerateJob.class);
    	
    	job.reratePlayers();
    
    }
}
