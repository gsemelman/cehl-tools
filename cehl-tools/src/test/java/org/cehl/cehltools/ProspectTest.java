package org.cehl.cehltools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cehl.cehltools.dto.ProspectDto;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DecodeTools;

public class ProspectTest {
	
	
	//.pct
	//Each prospect name = group of 16 bytes
	//Each team = 2700 bytes
	private static int RECORD_LENGTH = 22;
	private static int TEAM_LENGTH = 2700; //record length is 2700 but team can only fit 100 prospects
	private static int TEAM_LENGTH2 = 2200; //record length is 2700 but team can only fit 100 prospects
	
	public static void main(String[] args) throws Exception {
		ProspectTest test = new ProspectTest();
		
		test.run();
	}
	
	void run() throws IOException{
//		ProspectDto test1 = new ProspectDto(0, "Test1");
//		ProspectDto test2 = new ProspectDto(0, "Test2");
//		ProspectDto test3 = new ProspectDto(1, "Test3");
//		ProspectDto test4 = new ProspectDto(28, "Test4");
		
//		List<ProspectDto> list = new ArrayList<>();
//		list.add(test1);
//		list.add(test2);
//		list.add(test3);
//		list.add(test4);
//		
//		writeFile(list, new File("output\\cehl.pct"));
	}
	
//	public static void writeFile(List<ProspectDto> prospects, File outputFile) throws IOException{
//
//		Map<Integer,List<ProspectDto>> prospectMap = prospects.stream().collect(Collectors.groupingBy(ProspectDto::getTeamId)); 
//
//		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
//			for(Teams team : Teams.values()) {
//				List<ProspectDto> list = prospectMap.get(team.getTeamId());
//				int listSize = 0;
//				
//				if(list != null) {
//					listSize = list.size();
//					
//					if(list.size() > 100) {
//						throw new RuntimeException("Only 100 prospects supported per team");
//					}
//					
//					for(ProspectDto dto : list) {
//						byte[] bytes = new byte[RECORD_LENGTH];
//						DecodeTools.writeString(bytes, dto.getProspectName(), 0, 22);
//						bos.write(bytes);
//					}
//				}
//				
//
//				//add filler for unset records
//				int fillerBytes = TEAM_LENGTH - (listSize * RECORD_LENGTH);
//				
//				bos.write(new byte[fillerBytes]);
//
//			}
//		}
//		
//
//	
//		
//	}
}
