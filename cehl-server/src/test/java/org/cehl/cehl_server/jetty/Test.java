package org.cehl.cehl_server.jetty;

import java.io.File;
import java.util.List;

import org.cehl.model.cehl.player.Player;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;
import org.cehl.raw.transformer.RosterPlayerTransformer;


public class Test {

	public static void main(String[] args) throws Exception {
		RosterTools rosterTools = new RosterTools();
		RosterPlayerTransformer transformer = new RosterPlayerTransformer();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"),true);
		
		for(RosterRaw roster : rosterList){
			Player player = transformer.transformRoster(roster);

			System.out.println(player.toString());
		}
	}
}
