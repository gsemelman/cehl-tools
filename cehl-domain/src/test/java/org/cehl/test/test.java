package org.cehl.test;

import java.io.File;
import java.util.List;

import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.ProspectDecodeTools;
import org.cehl.raw.decode.RosterTools;

public class test {
	public static void main(String[] args) throws Exception {
		test importer = new test();
		
		importer.run();
	}
	
	void run(){
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"), false);
		List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));
		
		for(RosterRaw raw : rosterList){
			//System.out.println(raw);
			if(raw.getName().equals("Alex Galchenyuk")){
				System.out.println(raw);
			}
		}
		
		for(DrsRaw raw : drsList){
//			if(rosterExists(raw, rosterList)){
//				raw.setProFarmStatus1(255);
//				raw.setProFarmStatus2(255);
//			}
			if(raw.getName().equals("Alex Galchenyuk")){
				System.out.println(raw);
			}
			
		}
		
		//drsTools.writeDrs(drsList);

	}
	
	boolean rosterExists(DrsRaw raw, List<RosterRaw> rosterList){
		for(RosterRaw rosterRaw : rosterList){
			if(rosterRaw.getName().equals(raw.getName())){
				return true;
			}
		}
		return false;
	}
}
