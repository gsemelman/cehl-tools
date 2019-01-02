package org.cehl.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DecodeTools;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class RerateTest {
	public static void main(String[] args) throws Exception {
		RerateTest rerates = new RerateTest();
		
		rerates.run();
	}
	
	void run(){		
		List<RosterRaw> rosterList = RosterTools.loadRoster(new File("cehl.ros"), false);
		//List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));
		
		List<RosterRaw> rerateImportList = readWithCsvBeanReader();
		
		List<String> missingPlayers = new ArrayList<String>();
		
		//modify loaded attribs
		for(RosterRaw rawRerate : rerateImportList){
			RosterRaw rosterToUpdate = null;
			if(RosterTools.isDuplicate(rosterList, rawRerate.getName())){
				rosterToUpdate = RosterTools.findRosterByNameAndTeam(rosterList, rawRerate.getName(), rawRerate.getTeamId());
			}else{
				rosterToUpdate = RosterTools.findRosterByName(rosterList, rawRerate.getName());
			}
			
			if(rosterToUpdate == null){
				//throw new RuntimeException("Failure - Unable to update player:" + rawRerate.getName() + " As it cannot be found");
				missingPlayers.add(rawRerate.getName());
				continue;
			}
			
			rosterToUpdate.setIt(rawRerate.getIt());
			rosterToUpdate.setSp(rawRerate.getSp());
			rosterToUpdate.setSt(rawRerate.getSt());
			rosterToUpdate.setEn(rawRerate.getEn());
			rosterToUpdate.setDu(rawRerate.getDu());
			rosterToUpdate.setDi(rawRerate.getDi());
			rosterToUpdate.setSk(rawRerate.getSk());
			rosterToUpdate.setPa(rawRerate.getPa());
			rosterToUpdate.setPc(rawRerate.getPc());
			rosterToUpdate.setDf(rawRerate.getDf());
			rosterToUpdate.setSc(rawRerate.getSc());
			rosterToUpdate.setEx(rawRerate.getEx());
			rosterToUpdate.setLd(rawRerate.getLd());
			
			//attrib changes
			applyAttributeUpdates(rosterToUpdate);

		}
		
		//gather name list of roster for comparison
		Set<String> rosterNames = new HashSet<String>();
		for(RosterRaw rosterRaw : rosterList){
			rosterNames.add(rosterRaw.getName());
		}
		
		//gather drs entries not in roster
//		List<DrsRaw> drsToKeep = new ArrayList<DrsRaw>();
//		for(DrsRaw drsRaw : drsList){
//			if(!rosterNames.contains(drsRaw.getName())){
//				if(!drsRaw.getName().trim().isEmpty()){
//					drsToKeep.add(drsRaw);
//				}else{
//					System.out.println("blank drs");
//				}
//			}
//		}
//		
//		//generate new drs list
//		List<DrsRaw> newDrsList = new ArrayList<DrsRaw>();
//		newDrsList.addAll(drsToKeep);
//		for(RosterRaw rosterRaw : rosterList){
//			if(rosterRaw.getName().trim().isEmpty()){
//				continue;
//			}
//			
//			DrsRaw newDrs = new DrsRaw();
//			newDrs.setProFarmStatus1(255);
//			newDrs.setProFarmStatus2(255);
//			
//			try {
//				BeanUtils.copyProperties(newDrs, rosterRaw);
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			} catch (InvocationTargetException e) {
//				throw new RuntimeException(e);
//			}
//			
//			newDrsList.add(newDrs);
//		}
//		
//		//import prospects
//		
//		//sort new drs list
//		Collections.sort(newDrsList,
//                new Comparator<DrsRaw>()
//                {
//                    public int compare(DrsRaw d1, DrsRaw d2)
//                    {
//                        return d1.getName().compareTo(d2.getName());
//                    }        
//                });
		
		
		//write files
		RosterTools.writeRoster(rosterList);
		//drsTools.writeDrs(newDrsList);
		applyWashingtonSalary(new File("cehl.tms"));
		
		for(String missingPlayer: missingPlayers){
			System.out.println(missingPlayer);
		}
		
	}
	
	public static List<RosterRaw> readWithCsvBeanReader()  {
		
		List<RosterRaw> rosterList = new ArrayList<RosterRaw>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader("rerates.csv"),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			RosterRaw roster;
			while ((roster = beanReader.read(RosterRaw.class, header,
					processors)) != null) {
				rosterList.add(roster);
				System.out.println(roster.toString());
			}

		}catch (Exception e){
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
				new NotNull(new Trim()), // Name
				new NotNull(new ParseInt()), // TeamId
				new NotNull(new ParseInt()), //intensity
				new NotNull(new ParseInt()), //speed
				new NotNull(new ParseInt()), //strength
				new NotNull(new ParseInt()), //endurence
				new NotNull(new ParseInt()), //duribility
				new NotNull(new ParseInt()), //disciplie
				new NotNull(new ParseInt()), //skaing
				new NotNull(new ParseInt()), //pass accuracy
				new NotNull(new ParseInt()), //puck control
				new NotNull(new ParseInt()), //defense
				new NotNull(new ParseInt()), //scoring
				new NotNull(new ParseInt()), //experience
				new NotNull(new ParseInt()) //leadership
				
				
		};

		return processors;
	}
	
	
	void applyAttributeUpdates(RosterRaw roster){
//		if("Petr Mrazek".equals(roster.getName())){
//			roster.setSp(85);
//			roster.setSk(85);
//		}
//		else if("Leo Komarov".equals(roster.getName())){
//			roster.setEn(76);
//		}
//		else if("Matt Greene".equals(roster.getName())){
//			roster.setEn(79);
//		}

		if("Nikita Kucherov".equals(roster.getName())){
			roster.setContractLength(4);
		}
		if("Loui Eriksson".equals(roster.getName())){
			roster.setContractLength(2);
			roster.setSalary(4500000);
		}
	}
	
	void applyWashingtonSalary(File file) {

		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream("output/cehl.tms"));
		} catch (FileNotFoundException e1) {
			throw new RuntimeException();
		}
		 
		List<byte[]> byteList = DecodeTools.decodeFile(file,254);

		Integer newSalary = 13768249;
		
        for (byte[] bytes : byteList) {
        	String teamName = DecodeTools.readString(Arrays.copyOfRange(bytes,0, 10)).trim();
        	
        	if(teamName.equals("Washington")){
        		DecodeTools.writeInt(bytes,newSalary, 99, 103);
        	}

        }
        
        for (byte[] bytes : byteList) {
        	try {
				bos.write(bytes);
			} catch (IOException e) {
				throw new RuntimeException();
			}
        }
        
 
	}
	
}
