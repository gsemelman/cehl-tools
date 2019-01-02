package org.cehl.test;

import java.io.File;

import org.cehl.raw.decode.TeamDecodeTools;


public class TeamLoaderTest {
	public static void main(String[] args) throws Exception {
		TeamLoaderTest testLoader = new TeamLoaderTest();
		
		testLoader.run();
	}
	
	void run(){
		TeamDecodeTools tools = new TeamDecodeTools();
		
		tools.loadTeams(new File("cehl.tms"));
	}
	
}
