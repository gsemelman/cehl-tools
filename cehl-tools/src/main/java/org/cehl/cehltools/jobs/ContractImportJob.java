package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.ContractDto;
import org.cehl.commons.SimFileType;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
public class ContractImportJob extends AbstractJob {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContractImportJob.class);
	
	private File inputFile;
	
	public ContractImportJob() {
		super(JobType.CONTRACT_IMPORT);
	}

	@Override
	public void _run() {
		List<ContractDto> rerateImportList = null;
		try{
			rerateImportList = importContracts(inputFile.getAbsolutePath());
		}catch(SuperCsvConstraintViolationException e){
			logger.debug("error",e);
			
			this.addMessage(decodeImportException(e));
			
			return;
		}
		
		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);
		
		List<String[]> errorList = new ArrayList<String[]>();
		
		//modify loaded attribs
		for(ContractDto rawRerate : rerateImportList){
			RosterRaw rosterToUpdate = null;
			
			List<RosterRaw> playerSearch = RosterTools.findPlayerByName(rosterList, rawRerate.getName());
			
			if(playerSearch.isEmpty()) {
				//throw new RuntimeException("Failure - Unable to update player:" + rawRerate.getName() + " As it cannot be found");
				errorList.add(new String[] {rawRerate.getName(), "Player not found"});
				continue;
			}
			
			if(playerSearch.size() > 1) {
				logger.debug("Multiple players found. Searching by team name");
				CehlTeam team = CehlTeam.fromName(rawRerate.getTeamName());
				
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

			rosterToUpdate.setSalary(rawRerate.getSalary());
			rosterToUpdate.setContractLength(rawRerate.getContractLength());


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

				throw new RuntimeException("Error running import");
			} catch (Exception e) {
				logger.error("error",e);
			}
		}
	}

	@Override
	public String getJobInfo() {
		return "Import and apply contracts from csv";
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	public static List<ContractDto> importContracts(String fileName)  {
		
		List<ContractDto> rosterList = new ArrayList<ContractDto>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			ContractDto rerate;
			while ((rerate = beanReader.read(ContractDto.class, header,
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
				new NotNull(new ParseInt()), //contract
				new NotNull(new ParseInt()) //salary

				
		};

		return processors;
	}
	
	public static String decodeImportException(SuperCsvConstraintViolationException e){
		
		String message = "The %s attribute must be set for row: " + e.getCsvContext().toString();
		String messageNumeric = "The %s attribute must be set and must be numeric for row: " + e.getCsvContext().toString();
		
		switch(e.getCsvContext().getColumnNumber()){
		case 1:
			return String.format(message, "Player Name"); 
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
