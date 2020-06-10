package org.cehl.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cehl.raw.CoachRaw;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.TeamRaw;
import org.cehl.raw.decode.CoachDecodeTools;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.TeamDecodeTools;
import org.cehl.raw.transformer.coach.CoachImport;

public class CoachImporter {
	public static void main(String[] args) throws Exception {
		CoachImporter importer = new CoachImporter();
		
		importer.run();
	}
	
	void run(){

		List<CoachImport> coachImportList = CoachDecodeTools.importCoachesFromCsv("coaches2.csv");
		
		List<CoachRaw> coachList = new ArrayList<CoachRaw>();
		
		
		for(CoachImport coachImport : coachImportList){
			if(coachImport.getName() == null){
				continue;
			}
			
			CoachRaw coachRaw = new CoachRaw();

			coachRaw.setName(coachImport.getName());
			coachRaw.setOf(coachImport.getOf());
			coachRaw.setDf(coachImport.getDf());
			coachRaw.setEx(coachImport.getEx());
			coachRaw.setLd(coachImport.getLd());
			coachRaw.setSalary(coachImport.getSalary());
			
			if(coachImport.getTeamAbbr() != null){
				CehlTeam team = CehlTeam.fromAbbr(coachImport.getTeamAbbr());
				if(team == null){
					throw new RuntimeException("Unable to find team for abbreviate [" + coachImport.getTeamAbbr() + "]" );	
				}
				coachRaw.setTeamId(team.getTeamId());
			}else{
				coachRaw.setTeamId(255);
			}
			
			coachList.add(coachRaw);

		}
		
		Collections.sort(coachList,
                new Comparator<CoachRaw>()
                {
                    public int compare(CoachRaw d1, CoachRaw d2)
                    {
                        return d1.getName().compareTo(d2.getName());
                    }        
                });
		
		//set coachid
		int coachId = 1;
		for(CoachRaw coachRaw : coachList){
			coachRaw.setCoachId(coachId);
			coachId++;
			
			System.out.println(coachRaw.toString());
		}
		
		//write new coach file
		CoachDecodeTools.writeCoaches(coachList);
		
		//apply team salary
		applyTeamCoachSalary(new File("cehl.tms"),coachList);
		

	}
	
	void applyTeamCoachSalary(File file, List<CoachRaw> coachList) {

		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream("output/cehl.tms"));
		} catch (FileNotFoundException e1) {
			throw new RuntimeException();
		}
		 
		List<byte[]> byteList = DecodeTools.decodeFile(file,254);
		int teamId = 0;
		
        for (byte[] bytes : byteList) {
        	for(CoachRaw coach : coachList){
        		if(coach.getTeamId() == 255){
        			continue;
        		}
        		if(coach.getTeamId() == teamId){
        			//set name
        			DecodeTools.writeString(bytes, coach.getName(), 103, 22);
        			//bug in coach salary, multiply by 2 to get correct value in the sim app
        			Integer salary = coach.getSalary() * 2;
        			DecodeTools.writeInt(bytes,salary, 125, 129);
        			//set coachid
        			bytes[249] = (byte)coach.getCoachId();
        			//DecodeTools.writeInt(bytes,coach.getCoachId(), 249, 250);
        			continue;
        		}
        	}
        	
        	teamId++;
        }
        
        try {
            for (byte[] bytes : byteList) {
            	bos.write(bytes);
            }
		} catch (IOException e) {
			throw new RuntimeException();
		}finally{
	        try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
