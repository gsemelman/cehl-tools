package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

@Component
public class UnassignedCleanupJob extends AbstractJob{
	
	private static final Logger logger = LoggerFactory.getLogger(UnassignedCleanupJob.class);
	
	private File inputFile;
	
	public UnassignedCleanupJob(){
		super(JobType.UNASSIGNED_CLEANUP);
	}
	
	public UnassignedCleanupJob(File inputFile){
		this();
		this.inputFile = inputFile;
	}
	
	@Override
	public String getJobInfo() {
		return "Remove Players from unassigned list from csv file.";
	}
	
	@Override
	public List<String> additionalPreValidation(){
		List<String> validationMessages = new ArrayList<String>();
		if(inputFile == null){
			validationMessages.add("Error cleaning unassigned list, csv input file must be set");
		}else if(!inputFile.exists()){
			validationMessages.add("Error cleaning unassigned list, csv input file not exist at location :" +  getInputFilePath());
		}
		
		return validationMessages;
	}

	@Override
	public void _run() {

		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);
		List<DrsRaw> drsList = DrsTools.loadDrs(super.getLeagueFileByType(SimFileType.DRS_FILE));
		
		//List<String> deleteList = importDeletePlayerNameFile();
		List<String> deleteList = null;
		try{
			deleteList = importDeletePlayerNameFile();
		}catch(SuperCsvConstraintViolationException e){
			logger.error("error",e);

			this.addMessage("Player name must be between 1 and 22 characters for row: " + e.getCsvContext().toString());
			
			return;
		}
		
		if(deleteList.isEmpty()){
			this.addMessage("Job Cancelled: Delete list is empty. Output file will not be created");
			return;
		}

		int removeCount = 0;
        for(String deletePlayerName : deleteList){
        	DrsRaw drsRaw = getDrsByName(drsList,deletePlayerName);
        	if(drsRaw != null){
        		if(rosterListContains(rosterList,deletePlayerName)){
        			logger.info(deletePlayerName + " is rostered. Record will not be removed");
        			continue;
        		}else{
        			drsList.remove(drsRaw);
        			removeCount++;
        		}
        	}else{
        		logger.info("Cannot find player named [" + deletePlayerName + "] Player will not be removed");
        	}

        }
        
        if(removeCount > 0){
        	 File outputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.DRS_FILE));
             logger.info("Writing output file: " + outputFile.getAbsolutePath());
             DrsTools.writeDrs(drsList,outputFile);
        }else{
        	 this.addMessage("Was unable to remove any player from the inputted list.");
        	 this.addMessage("Players were either not found, or are currently rostered"
        	 		+ "");
        	 this.addMessage("Files will not be outputted");
        	 return;
        }

       
	}
	
	protected List<String> importDeletePlayerNameFile(){
		List<String> deleteList = new ArrayList<String>();

		ICsvListReader listReader = null;
		try {
			listReader = new CsvListReader(new FileReader(inputFile), CsvPreference.STANDARD_PREFERENCE);

			listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
			final CellProcessor[] processors = getProcessors();

			List<Object> player;
			while( (player = listReader.read(processors)) != null ) {

				logger.debug(String.format("lineNo=%s, rowNo=%s, playerList=%s", listReader.getLineNumber(),
						listReader.getRowNumber(), player));

				deleteList.add((String) player.get(0));
			}

		} catch(SuperCsvConstraintViolationException e){
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if( listReader != null ) {
				try {
					listReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return deleteList;
	}

	
	boolean rosterListContains(List<RosterRaw> rosterList, String name){
		for(RosterRaw raw : rosterList){
			if(raw.getName().equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	DrsRaw getDrsByName(List<DrsRaw> drsList, String name){
		for(DrsRaw raw : drsList){
			if(raw.getName().toUpperCase().equals(name.toUpperCase())){
				return raw;
			}
		}
		
		return null;
	}
	
	public CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new StrMinMax(1, 22, new Trim())) // Name
		};

		return processors;
	}
	
	private String getInputFilePath(){
		if(inputFile != null){
			return inputFile.getAbsolutePath();
		}
		
		return null;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

}
