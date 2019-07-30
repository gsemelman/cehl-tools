package org.cehl.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class RookieFix {
	public static void main(String[] args) throws Exception {
		RookieFix fix = new RookieFix();
		
		fix.run();
	}
	
	void run(){
		List<RosterRaw> rosterList = RosterTools.loadRoster(new File("cehl.ros"), false);
		List<RosterRaw> rosterListSeason22 = RosterTools.loadRoster(new File("cehl.22.ros"), true);
		List<RosterRaw> rosterList2 = RosterTools.loadRoster(new File("cehl.ros"), false);

		List<TeamPlayer> rookieList = new ArrayList<TeamPlayer>();
		
		//collect rookie players with less than 26 GP
		for(RosterRaw rosterRaw : rosterListSeason22){
			if(rosterRaw.getVetRookieStatus1() == 255){
				if(rosterRaw.getGamesPlayed() <= 25){
					System.out.println(rosterRaw);
					rookieList.add(new TeamPlayer(rosterRaw.getTeamId(), rosterRaw.getName()));
				}
			}
		}
		
		//reset rookie status on new roster file
		for(TeamPlayer rookie : rookieList){
			boolean found = false;
			
			for(RosterRaw rosterRaw : rosterList){
				if(rookie.getTeamId() == rosterRaw.getTeamId() && rookie.getPlayerName().equals(rosterRaw.getName())){
					rosterRaw.setVetRookieStatus1(255);
					rosterRaw.setVetRookieStatus2(255);
					
					found = true;
					break;
				}
			}
			
			if(!found){
				for(RosterRaw rosterRaw : rosterList){
					if(rookie.getPlayerName().equals(rosterRaw.getName())){
						rosterRaw.setVetRookieStatus1(255);
						rosterRaw.setVetRookieStatus2(255);
						
						System.out.println("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeamId() + " using name only for matching(found on teamId: "+ rosterRaw.getTeamId() +")"  );
						
						found = true;
						continue;
					}
				}
			}
			
			if(!found){
				//throw new RuntimeException("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeamId());
				System.out.println("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeamId());
			}else{
				
			}
		}

		//write files
		RosterTools.writeRoster(rosterList);
		//drsTools.writeDrs(newDrsList);
		//applyWashingtonSalary(new File("cehl.tms"));

	}
	
//	void applyWashingtonSalary(File file) {
//
//		BufferedOutputStream bos;
//		try {
//			bos = new BufferedOutputStream(new FileOutputStream("output/cehl.tms"));
//		} catch (FileNotFoundException e1) {
//			throw new RuntimeException();
//		}
//		 
//		List<byte[]> byteList = DecodeTools.decodeFile(file,254);
//
//		//Integer newSalary = 13768249;
//		Integer newSalary = 15582437;
//		
//        for (byte[] bytes : byteList) {
//        	String teamName = DecodeTools.readString(Arrays.copyOfRange(bytes,0, 10)).trim();
//        	
//        	if(teamName.equals("Washington")){
//        		DecodeTools.writeInt(bytes,newSalary, 99, 103);
//        	}
//
//        }
//        
//        for (byte[] bytes : byteList) {
//        	try {
//				bos.write(bytes);
//			} catch (IOException e) {
//				throw new RuntimeException();
//			}
//        }
//        
// 
//	}
	
	class TeamPlayer{

		private int teamId;
		private String playerName;
		
		public TeamPlayer(int teamId, String playerName) {
			super();
			this.teamId = teamId;
			this.playerName = playerName;
		}
		
		public int getTeamId() {
			return teamId;
		}
		public void setTeamId(int teamId) {
			this.teamId = teamId;
		}
		public String getPlayerName() {
			return playerName;
		}
		public void setPlayerName(String playerName) {
			this.playerName = playerName;
		}
	}
	
}
