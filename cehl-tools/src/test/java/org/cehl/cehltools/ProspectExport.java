package org.cehl.cehltools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cehl.cehltools.dto.ProspectDto;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DecodeTools;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

/*
 * Generate csv from pct file
 */
public class ProspectExport {
	
	
	private static int RECORD_LENGTH = 22;
	private static int TEAM_LENGTH = 2700; //record length is 2700 but team can only fit 100 prospects
	private static int TEAM_LENGTH2 = 2200; //record length is 2700 but team can only fit 100 prospects
	
	public static void main(String[] args) throws Exception {
		ProspectExport test = new ProspectExport();
		
		test.run();
	}
	
	void run() throws IOException{
		List<byte[]> byteList = DecodeTools.decodeFile(new File("C:/Temp/cehl.pct"),TEAM_LENGTH);

		List<ProspectDto> prospectList = new ArrayList<>();
		
		int teamId = 0;
		for(byte[] bytes: byteList) {
			for(int x = 0; x < 100 ; x++) { //only supports 100 prospects in the file so we can ignore the other filler bytes

				int from = x * RECORD_LENGTH;
				int to = from + RECORD_LENGTH;
				byte[] rawRecord = Arrays.copyOfRange(bytes, from, to);

				String prospectName = DecodeTools.readString(rawRecord);

				if(StringUtils.isNoneEmpty(prospectName)) {
					ProspectDto dto = new ProspectDto();
					Teams team = Teams.fromId(teamId);
					dto.setTeam(team);
					dto.setProspectName(DecodeTools.readString(rawRecord));

					prospectList.add(dto);
					
					System.out.println(dto);
				}


			}

			teamId++;

		}
		
   
		writeFile(prospectList, new File("c:/temp/prospects.csv"));


	}
	
	public static void writeFile(List<ProspectDto> prospectList, File outputFile) throws IOException{

	     ICsvBeanWriter beanWriter = null;
	        try {
	                beanWriter = new CsvBeanWriter(new FileWriter(outputFile),
	                        CsvPreference.STANDARD_PREFERENCE);
	                
	                // the header elements are used to map the bean values to each column (names must match)
	                final String[] header = new String[] { "Team", "ProspectName" };

	                final CellProcessor[] processors = new CellProcessor[] { 
	                        new NotNull(), // team name
	                        new NotNull(), // prosepct name
	                };
	                
	                
	                // write the header
	                beanWriter.writeHeader(header);
	                
	                // write the beans
	                for( final ProspectDto dto : prospectList ) {
	                        beanWriter.write(dto, header, processors);
	                }
	                
	        }
	        finally {
	                if( beanWriter != null ) {
	                        beanWriter.close();
	                }
	        }
		

	
		
	}
}
