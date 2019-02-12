package org.cehl.cehltools.jobs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.CashDto;
import org.cehl.commons.SimFileType;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.TeamDecodeTools;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

@Component
public class CashUpdaterJob extends AbstractJob {

	private static final Logger logger = Logger.getLogger(CashUpdaterJob.class);
	
	private File inputFile;
	
	public CashUpdaterJob() {
		super(JobType.TEAM_FINANCES);
	}

	@Override
	public void _run() {
		List<CashDto> cashImportList = null;
		try{
			cashImportList = importCash(inputFile.getAbsolutePath());
		}catch(SuperCsvConstraintViolationException e){
			logger.debug(e);
			
			this.addMessage(decodeImportException(e));
			
			return;
		}

		List<String[]> errorList = new ArrayList<String[]>();
		
		applyTeamCash(super.getLeagueFileByType(SimFileType.TEAM_FILE), cashImportList);
		
		if(!errorList.isEmpty()) {
			try {
				writeErrorLog(errorList);
				throw new RuntimeException("Error running import");
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	void applyTeamCash(File tmsFile, List<CashDto> cashList) {

		File teamOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.TEAM_FILE));
		

		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(teamOutputFile))){

			List<byte[]> byteList = DecodeTools.decodeFile(tmsFile,TeamDecodeTools.RECORD_LENGTH);
			int teamId = 0;
			
	        for (byte[] bytes : byteList) {
	        	for(CashDto cash : cashList){
	        		
	        		Teams team = Teams.fromName(cash.getTeamName());
	        		
	        		if(team == null){
	        			throw new RuntimeException("Invalid team name :" + cash.getTeamName());
	        		}
	        		if(team.getTeamId() == teamId){
	        			//set finances

	        			DecodeTools.writeInt(bytes,cash.getNewCash(), 99, 103);
	        			
	        			continue;
	        		}
	        	}
	        	
	        	teamId++;
	        }
	        
	        for (byte[] bytes : byteList) {
            	bos.write(bytes);
            }
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		 

	}

	@Override
	public String getJobInfo() {
		return "Import and apply team finances from csv";
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	public static List<CashDto> importCash(String fileName)  {
		
		List<CashDto> rosterList = new ArrayList<>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			CashDto rerate;
			while ((rerate = beanReader.read(CashDto.class, header,
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
				new StrNotNullOrEmpty(), // TeamName
				new NotNull(new ParseInt()), //team finances

				
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
