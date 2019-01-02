package org.cehl.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;

public class ProspectImporter {
	public static void main(String[] args) throws Exception {
		ProspectImporter importer = new ProspectImporter();
		
		importer.run();
	}
	
	void run(){
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"), false);
		List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));

		//gather name list of roster for comparison
		Set<String> rosterNames = new HashSet<String>();
		for(RosterRaw rosterRaw : rosterList){
			rosterNames.add(rosterRaw.getName());
		}
		
		//drs name list for comparison
		Set<String> drsNames = new HashSet<String>();

		//gather drs entries not in roster
		List<DrsRaw> drsToKeep = new ArrayList<DrsRaw>();
		for(DrsRaw drsRaw : drsList){
			if(!rosterNames.contains(drsRaw.getName())){
				if(!drsRaw.getName().trim().isEmpty()){
					drsToKeep.add(drsRaw);
					drsNames.add(drsRaw.getName());
				}else{
					System.out.println("blank drs");
				}
			}
		}
		
		//generate new drs list
		List<DrsRaw> newDrsList = new ArrayList<DrsRaw>();
		newDrsList.addAll(drsToKeep);
		for(RosterRaw rosterRaw : rosterList){
			
			//update rookie status
			rosterRaw.setVetRookieStatus1(0);
			rosterRaw.setVetRookieStatus2(0);
			
			if(rosterRaw.getName().trim().isEmpty()){
				continue;
			}
			
			DrsRaw newDrs = new DrsRaw();
			newDrs.setProFarmStatus1(255);
			newDrs.setProFarmStatus2(255);
			
			try {
				BeanUtils.copyProperties(newDrs, rosterRaw);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
			newDrsList.add(newDrs);
		}
		
		//import prospects
    	List<DrsRaw> playerImportList = drsTools.readWithCsvBeanReader("cehlImport.csv");
    	List<String> existingImportExceptions = new ArrayList<String>();
    	Iterator<DrsRaw> iter = playerImportList.iterator();
	   	while (iter.hasNext()) {
	   		DrsRaw drs = iter.next();
	   		if(rosterNames.contains(drs.getName())){
	   			existingImportExceptions.add(drs.getName());
	   			//iter.remove();
	   		}
	   		else if(drsNames.contains(drs.getName())){
	   			existingImportExceptions.add(drs.getName());
	   			//iter.remove();
	   		}
	   	        
	   	}
	   	
		newDrsList.addAll(playerImportList);
		
		//sort new drs list
		Collections.sort(newDrsList,
                new Comparator<DrsRaw>()
                {
                    public int compare(DrsRaw d1, DrsRaw d2)
                    {
                        return d1.getName().compareTo(d2.getName());
                    }        
                });
		
		
		//write files
		rosterTools.writeRoster(rosterList);
		drsTools.writeDrs(newDrsList);
		
		for(String existing: existingImportExceptions){
			System.out.println(existing);
		}
		
	}
	
}
