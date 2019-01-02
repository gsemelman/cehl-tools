package org.cehl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.TeamRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;
import org.cehl.raw.decode.TeamDecodeTools;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;


public class RosterExporter {
	public static void main(String[] args) throws Exception {
		RosterExporter exporter = new RosterExporter();
		
		exporter.run();
	}
	
	void run(){
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		TeamDecodeTools teamTools = new TeamDecodeTools();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehlPLF.ros"), true);
		//List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));
		//List<TeamRaw> teamList = teamTools.loadTeams(new File("cehl.tms"));

		String[] rosHeader = new String[]{"teamId","name","age","contractLength","position","it","sp","st","en","du","di","sk","pa","pc","df","sc","ex","ld"};
		String[] drsHeader = new String[]{"name","proFarmStatus1"};
		String[] teamHeader = new String[]{"teamId","teamName"};
		String[] rosHeader2 = new String[]{"name","vetRookieStatus1","vetRookieStatus2"};
		
		//rosterTools.exportRosterRawToCsv(rosterList, rosHeader, new File("roster.csv"));
		exportToCsv(rosterList, rosHeader, new File("roster.csv"));
		//exportToCsv(drsList, drsHeader, new File("drs.csv"));
		//exportToCsv(teamList, teamHeader, new File("teams.csv"));
		//exportToCsv(rosterList, rosHeader2, new File("roster2.csv"));
	}
	
	void exportToCsv(List<?> list, String[] header, File outputFile){
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(outputFile),
					CsvPreference.STANDARD_PREFERENCE);
			// the header elements are used to map the values to the bean (names
			// must match)
			beanWriter.writeHeader(header);
			
			// write the beans
            for( final Object object : list ) {
                    beanWriter.write(object, header);  
            }

		}catch (Exception e){
			throw new RuntimeException(e);
		}
		finally {
			if (beanWriter != null) {
				try {
					beanWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	void exportToCsv2(List<Object[]> objects, String[] header, File outputFile){
		ICsvListWriter listWriter = null;
		try {
			listWriter = new CsvListWriter(new FileWriter(outputFile),
					CsvPreference.STANDARD_PREFERENCE);
			// the header elements are used to map the values to the bean (names
			// must match)
			listWriter.writeHeader(header);
			
			// write the beans
            for( final Object object : objects ) {
            	listWriter.write(object, header);  
            }

		}catch (Exception e){
			throw new RuntimeException(e);
		}
		finally {
			if (listWriter != null) {
				try {
					listWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
