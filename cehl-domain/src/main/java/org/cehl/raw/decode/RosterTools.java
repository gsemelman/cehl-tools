package org.cehl.raw.decode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.cehl.raw.RosterRaw;
import org.cehl.raw.CehlTeam;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class RosterTools {
	
	private static int RECORD_LENGTH = 86;
	
	public static List<RosterRaw> loadRoster(File file, boolean skipBlank) {

		List<byte[]> byteList = DecodeTools.decodeFile(file,RECORD_LENGTH);
		List<RosterRaw> rosterList = new ArrayList<RosterRaw>();
		
		int teamPlayerId = 0;
		int teamId = 0;
        
        for (byte[] bytes : byteList) {
        	
        	RosterRaw roster = new RosterRaw();
        	roster.setTeamId(teamId);
        	roster.setTeamPlayerId(teamPlayerId);
        	
        	roster.setName(DecodeTools.readString(Arrays.copyOfRange(bytes,0, 22)).trim());
        	roster.setPosition(DecodeTools.readByte(bytes[22]));
        	roster.setJersey(DecodeTools.readByte(bytes[23]));
        	roster.setVetRookieStatus1(DecodeTools.readByte(bytes[24]));
        	roster.setVetRookieStatus2(DecodeTools.readByte(bytes[25]));
        	roster.setHand(DecodeTools.readByte(bytes[26]));
        	roster.setHeight((DecodeTools.readByte(bytes[27])));
        	roster.setWeight((DecodeTools.readByte(bytes[28])));
        	roster.setAge(DecodeTools.readByte(bytes[29]));
        	roster.setInjStatus(DecodeTools.readByte(bytes[30]));
        	roster.setCondition(DecodeTools.readByte(bytes[31]));
        	roster.setIt(DecodeTools.readByte(bytes[32]));
        	roster.setSp(DecodeTools.readByte(bytes[33]));
        	roster.setSt(DecodeTools.readByte(bytes[34]));
        	roster.setEn(DecodeTools.readByte(bytes[35]));
        	roster.setDu(DecodeTools.readByte(bytes[36]));
        	roster.setDi(DecodeTools.readByte(bytes[37]));
        	roster.setSk(DecodeTools.readByte(bytes[38]));
        	roster.setPa(DecodeTools.readByte(bytes[39]));
        	roster.setPc(DecodeTools.readByte(bytes[40]));
        	roster.setDf(DecodeTools.readByte(bytes[41]));
        	roster.setSc(DecodeTools.readByte(bytes[42]));
        	roster.setEx(DecodeTools.readByte(bytes[43]));
        	roster.setLd(DecodeTools.readByte(bytes[44]));
        	roster.setFiller1(bytes[45]);
        	roster.setSalary(DecodeTools.readInt(Arrays.copyOfRange(bytes,46, 50)));
        	roster.setContractLength(DecodeTools.readByte(bytes[50]));
        	roster.setSuspStatus(DecodeTools.readByte(bytes[51]));
        	roster.setGamesPlayed(DecodeTools.readByte(bytes[52]));
        	roster.setGoals(DecodeTools.readByte(bytes[53]));
        	roster.setAssists(DecodeTools.readByte(bytes[54]));
        	roster.setPlusMinus(DecodeTools.readByte(bytes[55]));
        	roster.setPlusMinus2(DecodeTools.readByte(bytes[56]));
        	roster.setPims(DecodeTools.readByte(bytes[57]));
        	roster.setPims2(DecodeTools.readByte(bytes[58]));
        	roster.setShots(DecodeTools.readByte(bytes[59]));
        	roster.setShots2(DecodeTools.readByte(bytes[60]));
        	roster.setPpGoals(DecodeTools.readByte(bytes[61]));
        	roster.setShGoals(DecodeTools.readByte(bytes[62]));
        	roster.setGwGoals(DecodeTools.readByte(bytes[63]));
        	roster.setGtGoals(DecodeTools.readByte(bytes[64]));
        	roster.setGoalStreak(DecodeTools.readByte(bytes[65]));
        	roster.setPointStreak(DecodeTools.readByte(bytes[66]));
        	roster.setFiller3(DecodeTools.readByte(bytes[67]));
        	roster.setFiller4(DecodeTools.readByte(bytes[68]));
        	roster.setFiller5(DecodeTools.readByte(bytes[69]));
        	roster.setFiller6(DecodeTools.readByte(bytes[70]));
        	roster.setFarmGoals(DecodeTools.readByte(bytes[71]));
        	roster.setFarmAssists(DecodeTools.readByte(bytes[72]));
        	roster.setFiller7(DecodeTools.readByte(bytes[73]));
        	roster.setFiller8(DecodeTools.readByte(bytes[74]));
        	roster.setFiller9(DecodeTools.readByte(bytes[75]));
        	roster.setBirthPlace(DecodeTools.readString(Arrays.copyOfRange(bytes,76, 79)));
        	roster.setFiller10(bytes[79]);
        	roster.setFiller11(bytes[80]);
        	roster.setFarmGamesPlayed(DecodeTools.readByte(bytes[81]));
        	roster.setFarmPim(DecodeTools.readByte(bytes[82]));
        	roster.setFarmPim2(DecodeTools.readByte(bytes[83]));
        	roster.setHits(DecodeTools.readByte(bytes[84]));
        	roster.setHits2(DecodeTools.readByte(bytes[85]));

        	//System.out.println(roster.toString());
        	
        	if(skipBlank){
            	if(roster.getName().length() > 0){
                	rosterList.add(roster);
            	}else{
            		System.out.println("Skipping blank player");
            	}
        	}else{
        		rosterList.add(roster);
        	}


        	//incrementIds();
    		teamPlayerId++; 
    		//50 players per team max, index starting at 0
    	  	if(teamPlayerId > 49){
    	  		teamPlayerId = 0;
    	  		teamId++;
    	  	}
        }
        
        return rosterList;
	}
	
	public static void writeRoster(List<RosterRaw> rosterList){
		writeRoster(rosterList, new File("output\\cehl.ros"));
	}
	
	public static void writeRoster(List<RosterRaw> rosterList, File outputFile){

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			//List<byte[]> byteList = new ArrayList<byte[]>();
			for (RosterRaw roster : rosterList) {
				byte[] bytes = new byte[RECORD_LENGTH];
				DecodeTools.writeString(bytes, roster.getName(), 0, 22);
				bytes[22] = (byte) roster.getPosition();
				bytes[23] = (byte) roster.getJersey();
				bytes[24] = (byte) roster.getVetRookieStatus1();
				bytes[25] = (byte) roster.getVetRookieStatus2();
				bytes[26] = (byte) roster.getHand();
				bytes[27] = (byte) roster.getHeight();
				bytes[28] = (byte) roster.getWeight();
				bytes[29] = (byte) roster.getAge();
				bytes[30] = (byte) roster.getInjStatus();
				bytes[31] = (byte) roster.getCondition();
				bytes[32] = (byte) roster.getIt();
				bytes[33] = (byte) roster.getSp();
				bytes[34] = (byte) roster.getSt();
				bytes[35] = (byte) roster.getEn();
				bytes[36] = (byte) roster.getDu();
				bytes[37] = (byte) roster.getDi();
				bytes[38] = (byte) roster.getSk();
				bytes[39] = (byte) roster.getPa();
				bytes[40] = (byte) roster.getPc();
				bytes[41] = (byte) roster.getDf();
				bytes[42] = (byte) roster.getSc();
				bytes[43] = (byte) roster.getEx();
				bytes[44] = (byte) roster.getLd();
				bytes[45] = (byte) roster.getFiller1();
				DecodeTools.writeInt(bytes,roster.getSalary(), 46, 50);
				bytes[50] = (byte) roster.getContractLength();
				bytes[51] = (byte) roster.getSuspStatus();
				bytes[52] = (byte) roster.getGamesPlayed();
				bytes[53] = (byte) roster.getGoals();
				bytes[54] = (byte) roster.getAssists();
				bytes[55] = (byte) roster.getPlusMinus();
				bytes[56] = (byte) roster.getPlusMinus2();
				bytes[57] = (byte) roster.getPims();
				bytes[58] = (byte) roster.getPims2();
				bytes[59] = (byte) roster.getShots();
				bytes[60] = (byte) roster.getShots2();
				bytes[61] = (byte) roster.getPpGoals();
				bytes[62] = (byte) roster.getShGoals();
				bytes[63] = (byte) roster.getGwGoals();
				bytes[64] = (byte) roster.getGtGoals();
				bytes[65] = (byte) roster.getGoalStreak();
				bytes[66] = (byte) roster.getPointStreak();
				bytes[67] = (byte) roster.getFiller3();
				bytes[68] = (byte) roster.getFiller4();
				bytes[69] = (byte) roster.getFiller5();
				bytes[70] = (byte) roster.getFiller6();
				bytes[71] = (byte) roster.getFarmGoals();
				bytes[72] = (byte) roster.getFarmAssists();
				bytes[73] = (byte) roster.getFiller7();
				bytes[74] = (byte) roster.getFiller8();
				bytes[75] = (byte) roster.getFiller9();
				DecodeTools.writeString(bytes, roster.getBirthPlace(), 76, 3);
				bytes[79] = (byte) roster.getFiller10();
				bytes[80] = (byte) roster.getFiller11();
				bytes[81] = (byte) roster.getFarmGamesPlayed();
				bytes[82] = (byte) roster.getFarmPim();
				bytes[83] = (byte) roster.getFarmPim2();
				bytes[84] = (byte) roster.getHits();
				bytes[85] = (byte) roster.getHits2();
				
				bos.write(bytes);
				//System.out.println(roster.toString());
				
			}
			
			bos.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("Error Reading The File.");
			e.printStackTrace();
		}
		catch (Exception e){
			System.out.println("Unknown file processing error for file : ");
			throw new RuntimeException(e);
		}

	}

	public static void exportRosterRawToCsv(List<RosterRaw> rosterList, String[] header, File outputFile){
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(outputFile),
					CsvPreference.STANDARD_PREFERENCE);
			// the header elements are used to map the values to the bean (names
			// must match)
			beanWriter.writeHeader(header);
			
			// write the beans
            for( final RosterRaw roster : rosterList ) {
                    beanWriter.write(roster, header);  
                    beanWriter.write(roster, header);  
                    beanWriter.write(new RosterRaw(), header);  
            }

		}catch (Exception e){
			throw new RuntimeException(e);
		}
		finally {
			if (beanWriter != null) {
				try {
					beanWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static RosterRaw findRosterByName(List<RosterRaw> rosterList, String name){
//		for(RosterRaw ros : rosterList){
//			if(name.equals(ros.getName())){
//				return ros;
//			}
//		}
//		
//		return null;
		return rosterList.stream().filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
	
	public static boolean isDuplicate(List<RosterRaw> rosterList, String name){
		
		return rosterList.stream().filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase())).count() > 1;
//				
//		int counter = 0;
//		
//		for(RosterRaw ros : rosterList){
//			if(name.equals(ros.getName())){
//				counter++;
//			}
//		}
//		
//		return counter > 1;
	}
	public static RosterRaw findRosterByNameAndTeam(List<RosterRaw> rosterList, String name, CehlTeam team){
		return findRosterByNameAndTeam(rosterList, name, team.getTeamId());
	}
	
	public static RosterRaw findRosterByNameAndTeam(List<RosterRaw> rosterList, String name, int teamId){
		
		return rosterList.stream()
				.filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase()) && roster.getTeamId() == teamId)
				.findFirst().orElse(null);

	}
	
	public static List<RosterRaw> findPlayerByName(List<RosterRaw> rosterList , String name){
		return rosterList.stream()
				.filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase()))
				.collect(Collectors.toList());
		
	}
	
	public static List<RosterRaw> findPlayerByNameAndJersey(List<RosterRaw> rosterList , String name, int jersey){
		return rosterList.stream()
				.filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase()) && roster.getJersey() == jersey)
				.collect(Collectors.toList());
		
	}
	
	public static  List<RosterRaw>  findPlayerByNameAndTeam(List<RosterRaw> rosterList, String name, CehlTeam team){
		return findPlayerByName(rosterList, name).stream()
				.filter(roster -> roster.getTeamId() == team.getTeamId())
				.collect(Collectors.toList());
	}


}
