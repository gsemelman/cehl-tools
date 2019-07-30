package org.cehl.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.cehl.commons.SimFileType;
import org.cehl.raw.CoachRaw;
import org.cehl.raw.TeamRaw;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.TeamDecodeTools;


public class TeamLoaderTest {
	public static void main(String[] args) throws Exception {
		TeamLoaderTest testLoader = new TeamLoaderTest();
		
		testLoader.run();
	}
	
	void run(){
		
		//List<TeamRaw> teams = tools.loadTeams(new File("C:\\3_personal\\CEHL\\FHLSim\\cehl.tms"));
		//List<TeamRaw> teams2 = tools.loadTeams(new File("cehl.tms"));

		
		//List<byte[]> byteList = DecodeTools.decodeFile(new File("cehl.tms"),254);
		
		BufferedOutputStream bos;
		File outputFile;
		try {
			outputFile = new File("c://temp//cehl.tms");
			bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			List<byte[]> byteList = DecodeTools.decodeFile(new File("C:\\3_personal\\CEHL\\FHLSim\\cehl.tms"),254);
			
			
			
			for(int x = 0; x < byteList.size(); x++) {
//				if(x < 30 || x ==34) { //remove unused teams
//					
//					byte[] bytes = byteList.get(x);
//					
//					//byteList
//					bytes[97] = (byte)255; //reset to unassigned
//					bytes[98] = (byte)255; //reset to unassigned
//					
//					bos.write(bytes);
//				}
				
				byte[] bytes = byteList.get(x);
				
				//if(bytes[0] > 0) {
				if(x < 30 ) {
					System.out.println(x + ": Resetting team div/conf");
					//bytes[97] = (byte)255; //reset to unassigned
					//bytes[98] = (byte)255; //reset to unassigned
					bos.write(bytes);
				}else {
					System.out.println(x + ": Blank Team");
				}

				
			}
			
			bos.close();
			
		} catch (FileNotFoundException e1) {
			throw new RuntimeException();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	void run2() {
		

		List<TeamRaw> teams = TeamDecodeTools.loadTeams(new File("C:\\3_personal\\CEHL\\FHLSim.bak\\test.tms"));
		
		teams.toString();
	}
	
}
