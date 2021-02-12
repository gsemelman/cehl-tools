package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.RerateDto;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.commons.SimFileType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.GoalieStatProcessor;
import org.cehl.raw.decode.RatingProcessor;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
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
public class UnassignedRerateImportJob extends AbstractJob {

	private static final Logger logger = LoggerFactory.getLogger(UnassignedRerateImportJob.class);
	
	private File inputFile;
	
	public UnassignedRerateImportJob() {
		super(JobType.RERATE_IMPORT);
	}

	@Override
	public void _run() {
		List<RerateDto> rerateImportList = null;
		try{
			rerateImportList = importReratesFromCsv(inputFile.getAbsolutePath());
		}catch(SuperCsvConstraintViolationException e){
			logger.error("error",e);
			
			this.addMessage(decodeImportException(e));
			
			return;
		}

		List<DrsRaw> drsList = DrsTools.loadDrs(super.getLeagueFileByType(SimFileType.DRS_FILE));
		
		List<String[]> errorList = new ArrayList<String[]>();
		
		//modify loaded attribs
		for(RerateDto rawRerate : rerateImportList){
			DrsRaw rosterToUpdate = null;
			
			List<DrsRaw> playerSearch = DrsTools.findPlayerByName(drsList, rawRerate.getName());
			
			if(playerSearch.isEmpty()) {
				//throw new RuntimeException("Failure - Unable to update player:" + rawRerate.getName() + " As it cannot be found");
				errorList.add(new String[] {rawRerate.getName(), "Player not found"});
				continue;
			}
			
			if(playerSearch.size() > 1) {
				logger.debug("Multiple players found. Attempting to match by age");
				
				if(rawRerate.getAge() > 0) {
					
					for(DrsRaw rawSearch : playerSearch) {
						int diff = Math.abs(rawRerate.getAge() - rawSearch.getAge());
						
						if(diff == 0 || diff == 1) {
							rosterToUpdate = rawSearch;
							break;
						}

					}
					
				}
				
				if(rosterToUpdate == null) {
					errorList.add(new String[] {rawRerate.getName(), "Multiple players found."});
					continue;
				}
				
	
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
				rosterToUpdate.setIt(rawRerate.getIt());
			}
			if(rawRerate.getSp() > 0) {
				rosterToUpdate.setSp(rawRerate.getSp());
			}
			if(rawRerate.getSt() > 0) {
				rosterToUpdate.setSt(rawRerate.getSt());
			}
			if(rawRerate.getEn() > 0) {
				rosterToUpdate.setEn(rawRerate.getEn());
			}
			if(rawRerate.getDu() > 0) {
				rosterToUpdate.setDu(rawRerate.getDu());
			}
			if(rawRerate.getDi() > 0) {
				rosterToUpdate.setDi(rawRerate.getDi());
			}
			if(rawRerate.getSk() > 0) {
				rosterToUpdate.setSk(rawRerate.getSk());
			}
			if(rawRerate.getPa() > 0) {
				rosterToUpdate.setPa(rawRerate.getPa());
			}
			if(rawRerate.getPc() > 0) {
				rosterToUpdate.setPc(rawRerate.getPc());
			}
			if(rawRerate.getDf() > 0) {
				rosterToUpdate.setDf(rawRerate.getDf());
			}
			if(rawRerate.getSc() > 0) {
				rosterToUpdate.setSc(rawRerate.getSc());
			}
			if(rawRerate.getEx() > 0) {
				rosterToUpdate.setEx(rawRerate.getEx());
			}
			if(rawRerate.getLd() > 0) {
				rosterToUpdate.setLd(rawRerate.getLd());
			}
			

		}
		
		
		//output files
		File drsOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.DRS_FILE));
		logger.info("Writing roster output file: " + drsOutputFile.getAbsolutePath());

		DrsTools.writeDrs(drsList, drsOutputFile);


		if(!errorList.isEmpty()) {
			try {
				writeErrorLog(errorList);
			} catch (Exception e) {
				logger.error("error",e);
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
				new NotNull(new ParseInt()), // jersey
				new NotNull(new ParseInt()), // age
				new NotNull(new RatingProcessor()), //intensity
				new NotNull(new RatingProcessor()), //speed
				new NotNull(new RatingProcessor()), //strength
				new NotNull(new RatingProcessor()), //endurence
				new NotNull(new RatingProcessor()), //duribility
				new NotNull(new RatingProcessor()), //disciplie
				new NotNull(new RatingProcessor()), //skaing
				new NotNull(new RatingProcessor()), //pass accuracy
				new NotNull(new RatingProcessor()), //puck control
				new NotNull(new GoalieStatProcessor(new ParseInt())), //defense
				new NotNull(new GoalieStatProcessor(new ParseInt())), //scoring
				new NotNull(new RatingProcessor()), //experience
				new NotNull(new RatingProcessor()) //leadership
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
