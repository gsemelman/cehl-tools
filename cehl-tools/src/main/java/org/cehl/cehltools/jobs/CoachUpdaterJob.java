package org.cehl.cehltools.jobs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.cehl.cehltools.App;
import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.cehl.raw.CoachRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.CoachDecodeTools;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.transformer.coach.CoachImport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.supercsv.exception.SuperCsvConstraintViolationException;

//@ConditionalOnProperty(value="#{systemProperties.coachUpdate}", matchIfMissing = false)
@Component
public class CoachUpdaterJob extends AbstractJob{
	
	private static final Logger logger = Logger.getLogger(CoachUpdaterJob.class);
	
	private File inputFile;

	public CoachUpdaterJob(){
		this(null);
	}
	
	public CoachUpdaterJob(File inputFile){
		super(JobType.COACH_UPDATE);
		this.inputFile = inputFile;
	}
	
	@Override
	public String getJobInfo() {
		return "Update coaches and set to teams from input csv file. [" + getInputFilePath() + "]";
	}
	
	@Override
	public List<String> additionalPreValidation(){
		List<String> validationMessages = new ArrayList<String>();
		if(inputFile == null){
			validationMessages.add("Error importing coaches, coach import csv must be set");
		}else if(!inputFile.exists()){
			validationMessages.add("Error importing coaches, coach import csv does not exist at location :" + getInputFilePath());
		}
		
		return validationMessages;
	}
	
	@Override
	public void _run(){
//		List<CoachImport> coachImportList = CoachDecodeTools.importCoachesFromCsv(
//				inputFile.getAbsolutePath());
		
		List<CoachImport> coachImportList = null;
		try{
			coachImportList = CoachDecodeTools.importCoachesFromCsv(inputFile.getAbsolutePath());
		}catch(SuperCsvConstraintViolationException e){
			logger.debug(e);
			
			this.addMessage(CoachDecodeTools.decodeImportException(e));
			
			return;
		}
		
		
		List<String> validationMessages = CoachDecodeTools.validateCoachImportRecords(coachImportList);
		if(!validationMessages.isEmpty()){
			logger.debug("Coach update csv files contains invalid records.");
			this.addMessage("Coach update csv files contains invalid records.");
			this.addMessages(validationMessages);
			return;
		}
		
		List<CoachRaw> coachList = new ArrayList<CoachRaw>();

		for(CoachImport coachImport : coachImportList){
			//do not import empty records
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
				Teams team = Teams.fromAbbr(coachImport.getTeamAbbr().toUpperCase());
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
			
			//logger.debug(coachRaw.toString());
		}

		File outputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.COACH_FILE));
		logger.info("Writing output file: " + outputFile.getAbsolutePath());
		//write new coach file
		CoachDecodeTools.writeCoaches(coachList,outputFile);
		
		//apply team salary
		applyTeamCoachSalary(super.getLeagueFileByType(SimFileType.TEAM_FILE),coachList);
		
		super.setFilesOutputted(true);
	}
	
	void applyTeamCoachSalary(File file, List<CoachRaw> coachList) {

		BufferedOutputStream bos;
		File outputFile;
		try {
			outputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.TEAM_FILE));
			bos = new BufferedOutputStream(new FileOutputStream(outputFile));
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
        	logger.info("Writing output file: " + outputFile.getAbsolutePath());
            for (byte[] bytes : byteList) {
            	bos.write(bytes);
            }
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
	        try {
				bos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
	
	String getInputFilePath(){
		if(inputFile != null){
			return inputFile.getAbsolutePath();
		}
		
		return null;
	}

	public File getCoachInputFile() {
		return inputFile;
	}

	public void setCoachInputFile(File coachInputFile) {
		this.inputFile = coachInputFile;
	}


}
