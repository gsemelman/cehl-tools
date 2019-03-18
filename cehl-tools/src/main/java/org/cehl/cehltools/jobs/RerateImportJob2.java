package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.RerateDto;
import org.cehl.commons.SimFileType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.GoalieStatProcessor;
import org.cehl.raw.decode.RatingProcessor;
import org.cehl.raw.decode.RosterTools;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

@Component
public class RerateImportJob2 extends AbstractJob {

	private static final Logger logger = Logger.getLogger(RerateImportJob2.class);
	
	private File inputFile;
	
	public RerateImportJob2() {
		super(JobType.RERATE_IMPORT);
	}

	@Override
	public void _run() {
		List<RerateDto> rerateImportList = null;
		try{
			rerateImportList = importReratesFromCsv(inputFile.getAbsolutePath());
		}catch(SuperCsvConstraintViolationException e){
			logger.debug(e);
			
			this.addMessage(decodeImportException(e));
			
			return;
		}
		
		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);
		List<DrsRaw> drsList = DrsTools.loadDrs(super.getLeagueFileByType(SimFileType.DRS_FILE));
		
		List<String[]> errorList = new ArrayList<String[]>();
		
		//modify loaded attribs
		for(RerateDto rawRerate : rerateImportList){
			RosterRaw rosterToUpdate = null;
			
			List<RosterRaw> playerSearch = RosterTools.findPlayerByName(rosterList, rawRerate.getName());
			
			if(playerSearch.isEmpty()) {
				//throw new RuntimeException("Failure - Unable to update player:" + rawRerate.getName() + " As it cannot be found");
				errorList.add(new String[] {rawRerate.getName(), "Player not found"});
				continue;
			}
			
			if(playerSearch.size() > 1) {
				logger.debug("Multiple players found. Searching by team name");
				Teams team = Teams.fromName(rawRerate.getTeamName());
				
				if(team == null) {
					logger.debug("Unknown team name");
					errorList.add(new String[] {rawRerate.getName(), "Unknown team name"});
				}

				List<RosterRaw> playerTeamSearch = RosterTools.findPlayerByNameAndTeam(playerSearch, rawRerate.getName(), team);
				if(playerTeamSearch.size() > 1) {
					logger.debug("Multiple players found on same team. Unable to rerate");
					errorList.add(new String[] {rawRerate.getName(), "Multiple players found on same team"});
					continue;
					
				}

				if(playerTeamSearch.size() == 1) {
					logger.debug("Player found with team search");
					rosterToUpdate = playerTeamSearch.get(0);
				}

			}else {
				rosterToUpdate = playerSearch.get(0);
			}
			
			if(rosterToUpdate== null) {
				logger.info("Unable to find player");
				errorList.add(new String[] {rawRerate.getName(), "Unable to find player"});
				continue;
			}

			if(rawRerate.getAge() > 0) {
				rosterToUpdate.setAge(rawRerate.getAge());
			}
			if(rawRerate.getIt() > 0) {
				rosterToUpdate.setAge(rawRerate.getIt());
			}
			if(rawRerate.getSp() > 0) {
				rosterToUpdate.setAge(rawRerate.getSp());
			}
			if(rawRerate.getSt() > 0) {
				rosterToUpdate.setAge(rawRerate.getSt());
			}
			if(rawRerate.getEn() > 0) {
				rosterToUpdate.setAge(rawRerate.getEn());
			}
			if(rawRerate.getDu() > 0) {
				rosterToUpdate.setAge(rawRerate.getDu());
			}
			if(rawRerate.getDi() > 0) {
				rosterToUpdate.setAge(rawRerate.getDi());
			}
			if(rawRerate.getSk() > 0) {
				rosterToUpdate.setAge(rawRerate.getSk());
			}
			if(rawRerate.getPa() > 0) {
				rosterToUpdate.setAge(rawRerate.getPa());
			}
			if(rawRerate.getPc() > 0) {
				rosterToUpdate.setAge(rawRerate.getPc());
			}
			if(rawRerate.getDf() > 0) {
				rosterToUpdate.setAge(rawRerate.getDf());
			}
			if(rawRerate.getSc() > 0) {
				rosterToUpdate.setAge(rawRerate.getSc());
			}
			if(rawRerate.getEx() > 0) {
				rosterToUpdate.setAge(rawRerate.getEx());
			}
			if(rawRerate.getLd() > 0) {
				rosterToUpdate.setAge(rawRerate.getLd());
			}
			

		}
		
		
		//output files
		File rosterOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.ROSTER_FILE));
		logger.info("Writing roster output file: " + rosterOutputFile.getAbsolutePath());
		RosterTools.writeRoster(rosterList,rosterOutputFile);
		
//		File drsOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.DRS_FILE));
//		logger.info("Writing drs output file: " + rosterOutputFile.getAbsolutePath());
//		DrsTools.writeDrs(newDrsList,drsOutputFile);
		
		if(!errorList.isEmpty()) {
			try {
				writeErrorLog(errorList);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public String getJobInfo() {
		return "Import and apply rerates from csv";
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	public static List<RerateDto> importReratesFromCsv(String fileName)  {
		
		List<RerateDto> rosterList = new ArrayList<RerateDto>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			RerateDto rerate;
			while ((rerate = beanReader.read(RerateDto.class, header,
					processors)) != null) {
				rosterList.add(rerate);
				System.out.println(rerate.toString());
			}

		}catch(SuperCsvConstraintViolationException e){
			throw e;
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return rosterList;
	}


	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new StrNotNullOrEmpty(new StrMinMax(1, 22, new Trim())), // Name
				new StrNotNullOrEmpty(), // TeamName
				new NotNull(new RatingProcessor()), //rating
	
				
				
		};

		return processors;
	}
	
	public static String decodeImportException(SuperCsvConstraintViolationException e){
		
		String message = "The %s attribute must be set for row: " + e.getCsvContext().toString();
		String messageNumeric = "The %s attribute must be set and must be numeric for row: " + e.getCsvContext().toString();
		
		switch(e.getCsvContext().getColumnNumber()){
		case 1:
			return String.format(message, "Player Name");
		case 2:
			return String.format(messageNumeric, "OF");
		case 3:
			return String.format(messageNumeric, "DF");
		case 4:
			return String.format(messageNumeric, "EX");
		case 5:
			return String.format(messageNumeric, "DF");
		case 7:
			return "Team Abbreviation must be between 2 and 3 characters if set for row:" + e.getCsvContext().toString();    
		default:
			return "Unknown issue for row:" + e.getCsvContext().toString();    	
    	}
		
	}
	
	private static void writeErrorLog(List<String[]> objects) throws IOException  {

		 ICsvListWriter listWriter = null;
	        try {
	                listWriter = new CsvListWriter(new FileWriter("output/errors.csv"),
	                        CsvPreference.STANDARD_PREFERENCE);

	                final String[] header = new String[] { "Name", "Error"};
	                
	                // write the header
	                listWriter.writeHeader(header);
	                
	                
	                for(String[] object : objects) {
	                	listWriter.write(object);
	                }

	                
	        }
	        finally {
	                if( listWriter != null ) {
	                        listWriter.close();
	                }
	        }
	}

}
