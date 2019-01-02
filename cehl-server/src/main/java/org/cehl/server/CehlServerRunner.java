/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cehl.server;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.cehl.model.cehl.player.Player;
import org.cehl.orm.dao.IGenericDao;
import org.cehl.orm.service.PersistenceService;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;
import org.cehl.raw.transformer.RosterPlayerTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.cehl")
public class CehlServerRunner {

	private static final Logger logger = Logger.getLogger(CehlServerRunner.class);

	@Autowired
	@Qualifier("playerDao")
	private IGenericDao playerDao;
	
	public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(CehlServerRunner.class);
        app.setBannerMode(Banner.Mode.OFF);
        ApplicationContext ctx = app.run(args);

		CehlServerRunner runner = new CehlServerRunner();
		//runner.runTestInsert(ctx);
	}
	
	void runTestInsert(ApplicationContext ctx){
		
		PersistenceService<Player, Long> playerService = (PersistenceService) ctx.getBean("playerService");
		
		
		RosterTools rosterTools = new RosterTools();
		RosterPlayerTransformer transformer = new RosterPlayerTransformer();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"),true);
		
		int counter = 1;
		for(RosterRaw roster : rosterList){
			Player player = transformer.transformRoster(roster);
			
			logger.info("saving player: " + player.getPlayerName() 
					+ " [" + counter + " of " + String.valueOf(rosterList.size()) + "]" );

			playerService.save(player);

			counter++;
		}
	}

}
