package org.cehl.test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.cehl.raw.RosterRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.RosterTools;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class WaiverOutput {
	public static void main(String[] args) throws Exception {
		WaiverOutput fix = new WaiverOutput();
		
		fix.run();
	}
	
	void run(){
		List<RosterRaw> rosterListSeason26 = RosterTools.loadRoster(new File("c:/temp/cehl.26.ros"), true);
		List<RosterRaw> rosterList2 = RosterTools.loadRoster(new File("C:\\3_personal\\CEHL\\FHLSim\\cehl.ros"), false);

		List<RosterRaw> filtered = new ArrayList<>();
		
		
		for(RosterRaw rosterRaw : rosterList2){
			if(rosterRaw.getInjStatus() == 0 && rosterRaw.getAge() >= 24 && rosterRaw.getContractLength() > 0){
//				if(rosterRaw.getGamesPlayed() >= 25){
//					System.out.println(rosterRaw);
//					rookieList.add(new TeamPlayer(rosterRaw.getTeamId(), rosterRaw.getName()));
//				}
				
				
				List<RosterRaw> lastSeasonList = RosterTools.findPlayerByNameAndTeam(
						rosterListSeason26, rosterRaw.getName(), Teams.fromId(rosterRaw.getTeamId()));
				if(lastSeasonList.isEmpty()) {
					lastSeasonList = RosterTools.findPlayerByName(rosterListSeason26, rosterRaw.getName());
				}
				
				if(!lastSeasonList.isEmpty()) {
					RosterRaw rosterLast = lastSeasonList.get(0);
					
					if(rosterLast.getGamesPlayed() >= 25){

						filtered.add(rosterRaw);
						
						System.out.println(rosterRaw);
					}
					
				}else {
					
				}
			}
		}
		

		//collect rookie players with less than 26 GP

		try {
			writeWithCsvBeanWriter(filtered);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
		
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

		@Override
		public String toString() {
			return "TeamPlayer [teamId=" + teamId + ", playerName=" + playerName + "]";
		}
		
		
	}

	private void writeWithCsvBeanWriter(List<RosterRaw> rosters) throws Exception {
        
        
        ICsvBeanWriter beanWriter = null;
        try {
                beanWriter = new CsvBeanWriter(new FileWriter("c:/temp/waiverDraft.csv"),
                        CsvPreference.STANDARD_PREFERENCE);
                
                // the header elements are used to map the bean values to each column (names must match)
                final String[] header = new String[] { "teamName","name","age","positionString","it","sp","st","en","du","di","sk","pa","pc","df","sc","ex","ld","salary","contractLength"};
       
                // write the header
                beanWriter.writeHeader(header);
                
                // write the beans
                for( final RosterRaw customer : rosters ) {
                        beanWriter.write(customer, header);
                }
                
        }
        finally {
                if( beanWriter != null ) {
                        beanWriter.close();
                }
        }
}
}
