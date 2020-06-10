package org.cehl.cehltools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.cehl.cehltools.dto.DraftPickDto;
import org.cehl.model.cehl.team.Team;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.ProspectDecodeTools.ProspectImport;

public class DraftPickTest {
	
	private static int DRAFT_ROUNDS = 4; //total rounds used in cehl
	private static int TOTAL_DRAFT_ROUNDS = 9; //total possible rounds
	private static int TOTAL_DRAFT_YEARS = 5; //total possible rounds
	
	//.pct
	//Each prospect name = group of 6 bytes
	//Each team = 1536 bytes
	//each team has 256 lines
	//each team has 5 years of picks 
	//each draft has up to 9 rounds.
	//first team has a single control(blank record) at the start(first line is empty), all other teams have 2. (first two lines are empty)
	private static int RECORD_LENGTH = 6;
	private static int TEAM_LENGTH = 1536; //record length is 2700 but team can only fit 100 prospects
	
	public static void main(String[] args) throws Exception {
		DraftPickTest test = new DraftPickTest();
		
		test.readTest();
	}
	
	void readTest() {
		List<byte[]> byteList = DecodeTools.decodeFile(new File("C:/Temp/cehl.dpk"),RECORD_LENGTH);
		
		List<DraftPickDto> pickList = new ArrayList<>();
		List<DraftPickDto> newpickList = new ArrayList<>();
		List<String> output = new ArrayList<>();
		int lastYear = 0;
		int lineCount = 1;
		int count = 1;
		int teamid = 0;
		int teamCount = 1;
		CehlTeam team = CehlTeam.ANA;
		System.out.println(team);
	   for (byte[] bytes : byteList) {
		   
		   
		   
		   DraftPickDto dto = new DraftPickDto();
		   dto.setTeam(team);
		   dto.setYear(DecodeTools.readByte(bytes[0]));
		   dto.setSelection(DecodeTools.readByte(bytes[1]));
		   dto.setTeamId(DecodeTools.readByte(bytes[2]));
		   dto.setNewTeamId(DecodeTools.readByte(bytes[3]));
		   dto.setActive(DecodeTools.readShort(Arrays.copyOfRange(bytes,4, 6)));
		   //dto.setNewTeamId(DecodeTools.readShort(Arrays.copyOfRange(bytes,3, 6)));
		   
			if(lastYear > dto.getSelection()) {
				//System.out.println("draft year");
				lineCount = 1;
			}
			
			

		   
			System.out.println(count + "-" +teamCount +"-"+ lineCount + "-" + dto);
			output.add(count + "-" +teamCount +"-"+ lineCount + "-" + dto);;
			
			
			if(dto.isActive() == -1) {
				//output.add(count + "-" +teamCount +"-"+ lineCount + "-" + dto);;
				newpickList.add(dto);
			}

		   
			lastYear = dto.getSelection();
			lineCount++;
			count++;
			teamCount++;
			
			pickList.add(dto);
			
			if(count % 256 == 0) {
				teamid++;
				teamCount=1;
				team = CehlTeam.fromId(teamid);
				System.out.println(team);
			}
	   }
	   

		File csvOutputFile = new File("c:/test/draft2.txt");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			output.stream()
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		output(newpickList);
		
		System.out.println("end of test");
	}
	
	void output(List<DraftPickDto> picks) {
		
		List<DraftPickDto> newList = new ArrayList<>();
		
		//create map by team.
		Map<CehlTeam, List<DraftPickDto>> picksMap = picks.stream()
				.filter(p-> p.getTeam() != null)
				.collect(Collectors.groupingBy(DraftPickDto::getTeam));
		
//		List<CehlTeam> teamsToProcess = Arrays.asList(CehlTeam.values());
//		teamsToProcess.sort((p1, p2) -> p1.getTeamId().compareTo(p1.getTeamId()));
//		
		//order of map doesnt matter because the following loop does
		// so by teamid. //assumes cehlTeam is listed in order.
		for(CehlTeam team: CehlTeam.values()) {
			List<DraftPickDto> teamPicks = picksMap.get(team);
			
			//remove previous year (season == 0)
			Iterator<DraftPickDto> itr = teamPicks.iterator();
			while(itr.hasNext()) {
				DraftPickDto dto = itr.next();
				
				if(dto.getYear() == 0 && dto.isActive() == -1) {
					itr.remove();
				}else {
					dto.setYear(dto.getYear()-1);
				}
			}
			
			//append blank records at start of team
			teamPicks.add(0, getBlankRecord(team));

			//add new draft year
			for(int x = 0 ; x <=8 ;x++) {
				DraftPickDto newDraftYear = new DraftPickDto();
				
				newDraftYear.setTeam(team);
				newDraftYear.setTeamId(team.getTeamId());
				newDraftYear.setYear(TOTAL_DRAFT_YEARS - 1 ); //assumes we are adding last year (current year is index 0 so remove 1)
				newDraftYear.setSelection(x);
				newDraftYear.setNewTeamId(team.getTeamId());
				newDraftYear.setActive(-1); //active record

				
				teamPicks.add(newDraftYear);
			}

			//add remaining blank records to fill out bytes for team.
			for(int x = teamPicks.size() ; x <= 255 ;x++) {
				teamPicks.add(getBlankRecord(team));
			}

			newList.addAll(teamPicks);
			System.out.println(teamPicks.size());
			
		}
		
		
	
		File csvOutputFile = new File("c:/test/draft.txt");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			newList.stream()
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		File outputFile = new File("c:/test/cehl.dpk");
		writePicks(newList,outputFile );
		
	}
	

	

	public static void writePicks(List<DraftPickDto> newList, File ouputFile){

	        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ouputFile.getAbsolutePath()))){
       
	            for(DraftPickDto dto : newList) {
	            	
	            	 byte[] bytes = new byte[RECORD_LENGTH];
	            	 bytes[0] = (byte) dto.getYear();
	            	 bytes[1] = (byte) dto.getSelection();
	            	 bytes[2] = (byte) dto.getTeamId();
	            	 bytes[3] = (byte) dto.getNewTeamId();
	     
	            	 DecodeTools.writeShort(bytes,(short) dto.isActive(), 4); 
	            	 
	            	 bos.write(bytes);
				}
				
   
	        } catch (FileNotFoundException e) {
	            System.out.println("File Not Found.");
	            throw new RuntimeException(e);
	        }
	        catch (IOException e) {
	            System.out.println("Error Reading The File.");
	            throw new RuntimeException(e);
	        }
	        catch (Exception e){
	            System.out.println("Unknown file processing error for file : ");
	            throw new RuntimeException(e);
	        }

	    }
	
	DraftPickDto getBlankRecord(CehlTeam team) {
		DraftPickDto blankRecord = new DraftPickDto();
		
		if(team != null) {
			blankRecord.setTeam(team);
			blankRecord.setTeamId(team.getTeamId());
		}
		
		blankRecord.setYear(0);
		blankRecord.setSelection(0);
		blankRecord.setNewTeamId(0);
		blankRecord.setActive(0);
		
		return blankRecord;
	}
	

}
