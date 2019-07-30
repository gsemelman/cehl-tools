package org.cehl.cehltools.jobs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.ProspectDto;
import org.cehl.commons.SimFileType;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.TeamNameProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Trim;
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
public class ProspectFileUpdaterJob extends AbstractJob {
	private static final Logger logger = LoggerFactory.getLogger(ProspectFileUpdaterJob.class);
	
	private static int RECORD_LENGTH = 22;
	//record length is 2700 but team can only fit 100 prospects (2200 bytes, so need to add filler of 500 bytes to end of each team)
	private static int TEAM_LENGTH = 2700; 
	
	
	private File inputFile;
	
	public ProspectFileUpdaterJob() {
		super(JobType.PROSPECT_FILE);
	}

	@Override
	public void _run() {
		List<ProspectDto> prospects = null;
		try{
			prospects = importProspects(inputFile);
		}catch(SuperCsvConstraintViolationException e){
			logger.error("error",e);
			
			this.addMessage(decodeImportException(e));
			
			return;
		}

		List<String[]> errorList = new ArrayList<String[]>();

		writeFile(prospects);
		
		if(!errorList.isEmpty()) {
			try {
				writeErrorLog(errorList);
				throw new RuntimeException("Error running import");
			} catch (Exception e) {
				logger.error("error",e);
			}
		}
	}
	
	
	public void writeFile(List<ProspectDto> prospects){
		
		File outputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.PROSPECT_FILE));

		Map<Teams,List<ProspectDto>> prospectMap = prospects.stream().collect(Collectors.groupingBy(ProspectDto::getTeam)); 

		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
			for(Teams team : Teams.values()) {
				List<ProspectDto> list = prospectMap.get(team);
				int listSize = 0;
				
				if(list != null) {
					listSize = list.size();
					
					if(list.size() > 100) {
						throw new RuntimeException("Only 100 prospects supported per team");
					}
					
					for(ProspectDto dto : list) {
						byte[] bytes = new byte[RECORD_LENGTH];
						DecodeTools.writeString(bytes, dto.getProspectName(), 0, 22);
						bos.write(bytes);
					}
				}
				

				//add filler for unset records
				int fillerBytes = TEAM_LENGTH - (listSize * RECORD_LENGTH);
				
				bos.write(new byte[fillerBytes]);

			}
		}catch(Exception e) {
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
	
	public static List<ProspectDto> importProspects(File file)  {
		
		List<ProspectDto> prospectList = new ArrayList<>();

		try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(file),
				CsvPreference.STANDARD_PREFERENCE);){
			

			// the header elements are used to map the values to the bean (names
			// must match)
			String[] header = beanReader.getHeader(true);

			final CellProcessor[] processors = getProcessors();
			
			ProspectDto prospect;
			while ((prospect = beanReader.read(ProspectDto.class, header,
					processors)) != null) {
				prospectList.add(prospect);
				System.out.println(prospect.toString());
			}

		}catch(SuperCsvConstraintViolationException e){
			throw e;
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		

		return prospectList;
	}


	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new TeamNameProcessor(), // TeamName
				new StrNotNullOrEmpty(new StrMinMax(1, 22, new Trim())), // Prospect Name
				
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
