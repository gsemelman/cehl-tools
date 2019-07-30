package org.cehl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;


public class DrsStatusFix {
	public static void main(String[] args) throws Exception {
		DrsStatusFix importer = new DrsStatusFix();
		
		importer.run();
	}
	
	List<String> messages = new ArrayList<String>();
	
	void run(){
		
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"), false);
		List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));

		//gather drs entries not in roster
		//List<DrsRaw> drsToKeep = new ArrayList<DrsRaw>();
		for(DrsRaw drsRaw : drsList){
			if(StringUtils.isEmpty(drsRaw.getName())) continue;
			
			for(RosterRaw rosterRaw : rosterList){
				if(drsRaw.getName().equals(rosterRaw.getName())){
					drsRaw.setProFarmStatus1(255);
					drsRaw.setProFarmStatus2(255);
				}
			}

		}
		
		

		
		//sort new drs list
//		Collections.sort(newDrsList,
//                new Comparator<DrsRaw>()
//                {
//                    public int compare(DrsRaw d1, DrsRaw d2)
//                    {
//                        return d1.getName().compareTo(d2.getName());
//                    }        
//                });
		
		

		
		if(messages.isEmpty()){
			//write files
			drsTools.writeDrs(drsList);
		}else{
			for(String message : messages){
				System.out.println(message);
			}
		}

		drsTools.writeDrs(drsList);
		
	}
	

	
}
