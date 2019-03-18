package org.cehl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;

public class RosterGoalieFix {
	public static void main(String[] args) throws Exception {
		RosterGoalieFix importer = new RosterGoalieFix();
		
		importer.run();
	}
	
	List<String> messages = new ArrayList<String>();
	
	void run(){
		
		RosterTools rosterTools = new RosterTools();

		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("c:/temp/cehl.ros"), false);

		for(RosterRaw roster: rosterList) {
			if(roster.getPosition() == 4) {
				roster.setSc(0);
				roster.setDf(0);
			}
		}

		RosterTools.writeRoster(rosterList, new File("c:/temp/fix/cehl.ros"));

		
	}
	

	
}
