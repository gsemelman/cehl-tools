package org.cehl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cehl.raw.CoachRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.CoachDecodeTools;
import org.cehl.raw.transformer.coach.CoachImport;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class CoachExport {
	public static void main(String[] args) throws Exception {
		CoachExport exporter = new CoachExport();
		
		exporter.run();
	}
	
	void run() throws IOException{
		List<CoachRaw> coachList = CoachDecodeTools.loadCoaches(new File("cehl.coa"));
		List<CoachImport> coachImport = new ArrayList<CoachImport>();
		
		for(CoachRaw coachRaw : coachList){
			CoachImport ci = new CoachImport();
			ci.setName(coachRaw.getName());
			ci.setDf(coachRaw.getDf());
			ci.setEx(coachRaw.getEx());
			ci.setLd(coachRaw.getLd());
			ci.setOf(coachRaw.getOf());
			ci.setSalary(coachRaw.getSalary() / 2);
			
			CehlTeam team = CehlTeam.fromId(coachRaw.getTeamId());		
			if(team!= null){
				ci.setTeamAbbr(team.name());
			}

			coachImport.add(ci);
		}
		
		ICsvBeanWriter beanWriter = null;
        try {
                beanWriter = new CsvBeanWriter(new FileWriter("output/coachOutput.csv"),
                        CsvPreference.STANDARD_PREFERENCE);

                // the header elements are used to map the bean values to each column (names must match)
                //final String[] header = new String[] { "coachId", "name", "of", "df", "ex", "ld", "salary", "teamId" };
                //final CellProcessor[] processors = CoachDecodeTools.getCoachRawProcessors();
                final String[] header = new String[] { "name", "of", "df", "ex", "ld", "salary", "teamAbbr" };
                final CellProcessor[] processors = CoachDecodeTools.getCoachImportProcessors();

                // write the header
                beanWriter.writeHeader(header);
                
                // write the beans
                for( final CoachImport ci : coachImport ) {
                        beanWriter.write(ci, header, processors);
                }
                
        }
        finally {
                if( beanWriter != null ) {
                        beanWriter.close();
                }
        }
	}
}
