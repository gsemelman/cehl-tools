package org.cehl.raw.decode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cehl.commons.StringUtils;
import org.cehl.raw.CoachRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.transformer.coach.CoachImport;
//import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CoachDecodeTools {
	
private static int RECORD_LENGTH = 31;

	public static List<CoachRaw> loadCoaches(File file) {

		List<byte[]> byteList = DecodeTools.decodeFile(file,RECORD_LENGTH);
		
		List<CoachRaw> coachList = new ArrayList<CoachRaw>();
        
        for (byte[] bytes : byteList) {
        	CoachRaw coach = new CoachRaw();
        	
        	coach.setName(DecodeTools.readString(Arrays.copyOfRange(bytes,0, 22)).trim());
        	coach.setOf(DecodeTools.readByte(bytes[22]));
        	coach.setDf(DecodeTools.readByte(bytes[23]));
        	coach.setEx(DecodeTools.readByte(bytes[24]));
        	coach.setLd(DecodeTools.readByte(bytes[25]));
        	coach.setSalary(DecodeTools.readInt(Arrays.copyOfRange(bytes,26, 30)));
        	coach.setTeamId(DecodeTools.readByte(bytes[30]));

        	coachList.add(coach);
        	
        	System.out.println(coach.toString());
        }
        
        return coachList;
	}
	
	public static void writeCoaches(List<CoachRaw> coachList){
		writeCoaches(coachList, new File("output\\cehl.coa"));
	}
	
	public static void writeCoaches(List<CoachRaw> coachList, File ouputFile){

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ouputFile.getAbsolutePath()));
			
			//List<byte[]> byteList = new ArrayList<byte[]>();
			for (CoachRaw coach : coachList) {
				byte[] bytes = new byte[RECORD_LENGTH];
				DecodeTools.writeString(bytes, coach.getName(), 0, 22);
				bytes[22] = (byte) coach.getOf();
				bytes[23] = (byte) coach.getDf();
				bytes[24] = (byte) coach.getEx();
				bytes[25] = (byte) coach.getLd();
				//bug in coach salary, multiply by 2 to get correct value in the sim app
				DecodeTools.writeInt(bytes,(coach.getSalary() * 2), 26, 30); 
				bytes[30] = (byte) coach.getTeamId().intValue();

				bos.write(bytes);
			}
			
			bos.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			System.out.println("Error Reading The File.");
			throw new RuntimeException(e);
		}
		catch (Exception e){
			System.out.println("Unknown file processing error for file : ");
			throw new RuntimeException(e);
		}

	}
	
	public static List<CoachImport> importCoachesFromCsv(String fileName)  {
		
		List<CoachImport> coachList = new ArrayList<CoachImport>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getCoachImportProcessors();
			
			CoachImport coach;
			while ((coach = beanReader.read(CoachImport.class, header,
					processors)) != null) {
				coachList.add(coach);
				//System.out.println(coach.toString());
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

		return coachList;
	}
	
	public static String decodeImportException(SuperCsvConstraintViolationException e){
		
		String message = "The %s attribute must be set for row: " + e.getCsvContext().toString();
		String messageNumeric = "The %s attribute must be set and must be numeric for row: " + e.getCsvContext().toString();
		
		switch(e.getCsvContext().getColumnNumber()){
		case 1:
			return String.format(message, "Coach Name");
		case 2:
			return String.format(messageNumeric, "OF");
		case 3:
			return String.format(messageNumeric, "DF");
		case 4:
			return String.format(messageNumeric, "EX");
		case 5:
			return String.format(messageNumeric, "DF");
		case 6:
			return String.format(messageNumeric, "Salary");
		case 7:
			return "Team Abbreviation must be between 2 and 3 characters if set for row:" + e.getCsvContext().toString();    
		default:
			return "Unknown issue for row:" + e.getCsvContext().toString();    	
    	}
		
	}
	
	public static List<String> validateCoachImportRecords(List<CoachImport> imports){
		List<String> messages = new ArrayList<String>();
		Set<String> teamAbbrSet = new HashSet<String>();
		
		for(CoachImport coachImport : imports){
			if(!ValidationHelper.validatePlayerName(coachImport.getName())){
				messages.add("Coach name cannot be empty and must be between 1 and 22 characters");
			}
			
			if(!ValidationHelper.validateBasicAttribute(coachImport.getDf())){
				messages.add("Record for name [" + coachImport.getName() + "] is invalid. DF attribute must be between 55 and 99");
			}
			if(!ValidationHelper.validateBasicAttribute(coachImport.getOf())){
				messages.add("Record for name [" + coachImport.getName() + "] is invalid. OF attribute must be between 55 and 99");
			}
			if(!ValidationHelper.validateBasicAttribute(coachImport.getEx())){
				messages.add("Record for name [" + coachImport.getName() + "] is invalid. EX attribute must be between 55 and 99");
			}
			if(!ValidationHelper.validateBasicAttribute(coachImport.getLd())){
				messages.add("Record for name [" + coachImport.getName() + "] is invalid. LD attribute must be between 55 and 99");
			}
			if(!ValidationHelper.validateSalary(coachImport.getSalary())){
				messages.add("Record for name [" + coachImport.getName() + "] is invalid. Coach salary must be between 400K and 20M");
			}
	
		    if(coachImport.getTeamAbbr() != null){
		    	if(teamAbbrSet.contains(coachImport.getTeamAbbr())){
		    		messages.add("Record for name [" + coachImport.getName() + "] is invalid. Team Abbreviation [" 
		    				+ coachImport.getTeamAbbr() + "] is already set for another coach");
		    	}
				
				if(!ValidationHelper.validateTeamAbbreviation(coachImport.getTeamAbbr())){
					messages.add("Record for name [" + coachImport.getName() + "] is invalid. Team Abbreviation [" 
							+ coachImport.getTeamAbbr() + "] does not correspond to any team. Please add a use a valid team");
				}
		    	teamAbbrSet.add(coachImport.getTeamAbbr());
		    }
			
		}
		
		Set<String> missingTeams = Teams.getMissingTeamsByAbbr(teamAbbrSet);
		if(!missingTeams.isEmpty()){
			messages.add("All teams require a coach. Coach not set for the following teams. [" 
					+ StringUtils.commaList(missingTeams) + "]");
		}
		
		return messages;
	}

	public static CellProcessor[] getCoachImportProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new StrMinMax(0, 22, new Trim())), // Name
				new NotNull(new ParseInt()), //of
				new NotNull(new ParseInt()), //df
				new NotNull(new ParseInt()), //ex
				new NotNull(new ParseInt()),//ld
				new NotNull(new ParseInt()), //salary
				new Optional(new StrMinMax(2, 3, new Trim())), // team abbr
		};

		return processors;
	}
	
	public static CellProcessor[] getCoachRawProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new ParseInt()), //id
				new NotNull(new StrMinMax(0, 22, new Trim())), // Name
				new NotNull(new ParseInt()), //of
				new NotNull(new ParseInt()), //df
				new NotNull(new ParseInt()), //ex
				new NotNull(new ParseInt()),//ld
				new NotNull(new ParseInt()), //salary
				new NotNull(new ParseInt()), //teamid
		};

		return processors;
	}

}
