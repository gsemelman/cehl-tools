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
import org.cehl.model.cehl.player.PlayerHandType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.ProspectRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.ProspectDecodeTools;
import org.cehl.raw.decode.RosterTools;

public class ProspectImporter2 {
	public static void main(String[] args) throws Exception {
		ProspectImporter2 importer = new ProspectImporter2();
		
		importer.run();
	}
	
	List<String> messages = new ArrayList<String>();
	
	void run(){
		
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		ProspectDecodeTools prospectTools = new ProspectDecodeTools();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"), false);
		List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));

		//gather name list of roster for comparison
		Set<String> rosterNames = new HashSet<String>();
		for(RosterRaw rosterRaw : rosterList){
			rosterNames.add(rosterRaw.getName());
		}
		
		Set<String> drsNames = new HashSet<String>();
		
		//gather drs entries not in roster
		//List<DrsRaw> drsToKeep = new ArrayList<DrsRaw>();
		for(DrsRaw drsRaw : drsList){
			if(!rosterNames.contains(drsRaw.getName())){
				if(!drsRaw.getName().trim().isEmpty()){
					//drsToKeep.add(drsRaw);
					drsNames.add(drsRaw.getName());
				}else{
					System.out.println("blank drs");
				}
			}
		}
		
//		//drs name list for comparison
//		Set<String> drsNames = new HashSet<String>();
//
//		//gather drs entries not in roster
//		List<DrsRaw> drsToKeep = new ArrayList<DrsRaw>();
//		for(DrsRaw drsRaw : drsList){
//			if(!rosterNames.contains(drsRaw.getName())){
//				if(!drsRaw.getName().trim().isEmpty()){
//					drsToKeep.add(drsRaw);
//					drsNames.add(drsRaw.getName());
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
//			
//			//update rookie status
//			rosterRaw.setVetRookieStatus1(0);
//			rosterRaw.setVetRookieStatus2(0);
//			
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
		
		List<DrsRaw> newDrsList = new ArrayList<DrsRaw>();
		newDrsList.addAll(drsList);
//		List<RosterRaw> newRosterList = new ArrayList<RosterRaw>();
		
		//import prospects
    	List<ProspectRaw> playerImportList = prospectTools.readWithCsvBeanReader("prospects.csv");
    	List<String> existingImportExceptions = new ArrayList<String>();
    	Iterator<ProspectRaw> iter = playerImportList.iterator();
	   	while (iter.hasNext()) {
	   		ProspectRaw prospect = iter.next();
	   		if(rosterNames.contains(prospect.getName())){
	   			existingImportExceptions.add(prospect.getName());
	   			//iter.remove();
	   		}
	   		else if(drsNames.contains(prospect.getName())){
	   			existingImportExceptions.add(prospect.getName());
	   			//iter.remove();
	   		}
	   		else{
		   		DrsRaw newDrs = DrsTools.createInstance();
		   		try {
					BeanUtils.copyProperties(newDrs,prospect);
					PlayerHandType handType = PlayerHandType.HandTypeByFlagValue(prospect.getHand());
					if(handType == null){
						throw new RuntimeException("unable to find hand type" + prospect.getHand());
					}
					newDrs.setHand(handType.rawValue());
					
					PlayerPositionType positionType = PlayerPositionType.PositionByFlagValue(prospect.getPosition());
					if(positionType == null){
						throw new RuntimeException("unable to find positionType type " + prospect.getPosition());
					}
					newDrs.setPosition(positionType.rawValue());
					
					newDrs.setProFarmStatus1(255); //if adding to team
					newDrs.setProFarmStatus2(255); //if adding to team
					
					newDrsList.add(newDrs);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}

		   		RosterRaw newRos = new RosterRaw();
		   		try {
					BeanUtils.copyProperties(newRos,newDrs);
					CehlTeam team = CehlTeam.valueOf(prospect.getTeamName());
					if(team == null){
						throw new RuntimeException("unable to find team " + prospect.getTeamName() );
					}
					newRos.setTeamId(team.getTeamId());
					addToRoster(rosterList, newRos);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
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
		
		

		
		for(String existing: existingImportExceptions){
			System.out.println(existing);
		}
		
		if(messages.isEmpty()){
			//write files
			//rosterTools.writeRoster(rosterList);
			//drsTools.writeDrs(newDrsList);
		}else{
			for(String message : messages){
				System.out.println(message);
			}
		}
		
		rosterTools.writeRoster(rosterList);
		drsTools.writeDrs(newDrsList);
		
	}
	
	public void getNextJerseyNumber(List<RosterRaw> rosterList, int teamId){
		Integer maxJersey = 0;
		
		for(RosterRaw rosRaw : rosterList){
			if(!rosRaw.getName().trim().isEmpty()){
				maxJersey = Integer.max(maxJersey, rosRaw.getJersey());
			}
		}
	}
	
	public void addToRoster(List<RosterRaw> rosterList, RosterRaw ros){
		Integer itemIndex = null;
		
		for(RosterRaw rosRaw : rosterList){
			if(rosRaw.getTeamId() == ros.getTeamId()){
				if(rosRaw.getName().trim().isEmpty()){
					itemIndex = rosterList.indexOf(rosRaw);
					break;
				}
			}
		}
		
		if(itemIndex != null){
			rosterList.set(itemIndex, ros);
		}else{
			//throw new RuntimeException("Unable to add player to roster, no available spots");	
			messages.add("Unable to add player to roster, no available spots [" 
			+ ros.getName() + "] [" + CehlTeam.fromId(ros.getTeamId()) + "]");
		}
		
		
	}
	
}
