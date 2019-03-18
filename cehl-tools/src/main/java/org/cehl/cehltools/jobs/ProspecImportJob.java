package org.cehl.cehltools.jobs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.cehl.model.cehl.player.PlayerHandType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.ProspectRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.ProspectDecodeTools;
import org.cehl.raw.decode.RosterTools;
import org.springframework.stereotype.Component;

@Component
public class ProspecImportJob extends AbstractJob{
	
	private static final Logger logger = Logger.getLogger(ProspecImportJob.class);
	
	private File inputFile;

	public ProspecImportJob(){
		super(JobType.PROSPECT_IMPORT);
	}
	
	public ProspecImportJob(File inputFile){
		this();
		this.inputFile = inputFile;
	}
	
	@Override
	public String getJobInfo() {
		return "Import players and set to teams from input csv file. [" + getInputFilePath() + "]";
	}
	
	@Override
	public List<String> additionalPreValidation(){
		List<String> validationMessages = new ArrayList<String>();
		if(inputFile == null){
			validationMessages.add("Error importing prospects, import csv must be set");
		}else if(!inputFile.exists()){
			validationMessages.add("Error importing prospects, import csv does not exist at location :" + getInputFilePath());
		}
		
		return validationMessages;
	}

	@Override
	public void _run() {	
		
    	List<ProspectRaw> playerImportList = ProspectDecodeTools.readWithCsvBeanReader(getInputFilePath());
		
		List<String> validationMessages = ProspectDecodeTools.validateProspectRecords(playerImportList);
		if(!validationMessages.isEmpty()){
			logger.debug("Prospect import csv file contains invalid records.");
			this.addMessage("Prospect import csv file contains invalid records.");
			this.addMessages(validationMessages);
			return;
		}
		
		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);
		List<DrsRaw> drsList = DrsTools.loadDrs(super.getLeagueFileByType(SimFileType.DRS_FILE));

		//gather name list of roster for comparison
		Set<String> rosterNames = new HashSet<String>();
		for(RosterRaw rosterRaw : rosterList){
			rosterNames.add(rosterRaw.getName());
		}
		
		Set<String> drsNames = new HashSet<String>();
		
		//gather drs entries not in roster
		for(DrsRaw drsRaw : drsList){
			if(!rosterNames.contains(drsRaw.getName())){
				if(!drsRaw.getName().trim().isEmpty()){
					//drsToKeep.add(drsRaw);
					drsNames.add(drsRaw.getName());
				}
			}
		}
		
		
		List<DrsRaw> newDrsList = new ArrayList<DrsRaw>();
		newDrsList.addAll(drsList);
//		List<RosterRaw> newRosterList = new ArrayList<RosterRaw>();
		
		//import prospects

    	List<String> existingImportExceptions = new ArrayList<String>();
    	Iterator<ProspectRaw> iter = playerImportList.iterator();
	   	while (iter.hasNext()) {
	   		ProspectRaw prospect = iter.next();
	   		if(rosterNames.contains(prospect.getName())){
	   			existingImportExceptions.add(prospect.getName());
	   		}
	   		else if(drsNames.contains(prospect.getName())){
	   			existingImportExceptions.add(prospect.getName());
	   		}
	   		else{
	   			
				//search by name first
				Teams team = Teams.fromName(prospect.getTeamName());
				//if name not found search by abbreviation
				if(team == null){
					team = Teams.fromAbbr(prospect.getTeamName());
				}

				if(team == null){
					//throw new RuntimeException("unable to find team " + prospect.getTeamName() );
					this.addMessage("unable to find team " + prospect.getTeamName() );
					continue;
				}
	   			
	   			int jersey = getNextJerseyNumber(rosterList,team.getTeamId()  );
	   			
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
					newDrs.setJersey(jersey);
					
					newDrs.setProFarmStatus1(255); //if adding to team
					newDrs.setProFarmStatus2(255); //if adding to team
					
					newDrsList.add(newDrs);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}

		   		RosterRaw newRos = new RosterRaw();
		   		try {
					BeanUtils.copyProperties(newRos,newDrs);
					//Teams team = Teams.valueOf(prospect.getTeamName());
					
//					//search by name first
//					Teams team = Teams.fromName(prospect.getTeamName());
//					//if name not found search by abbreviation
//					if(team == null){
//						team = Teams.fromAbbr(prospect.getTeamName());
//					}
//
//					if(team == null){
//						//throw new RuntimeException("unable to find team " + prospect.getTeamName() );
//						this.addMessage("unable to find team " + prospect.getTeamName() );
//					}
					newRos.setTeamId(team.getTeamId());
					addToRoster(rosterList, newRos);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
	   		}

	   	}
	   	
	   	
		for(String existing: existingImportExceptions){
			logger.info("Player: [" + existing + "] already exists and will not be added");
		}
	   	
	   	if(this.jobContainsErrors()){
	   		logger.debug("Error messages found. Exiting app");
	   		return;
	   	}

		//sort new drs list
	   	DrsTools.sortDrsListByName(newDrsList);


		//output files
		File rosterOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.ROSTER_FILE));
		logger.info("Writing roster output file: " + rosterOutputFile.getAbsolutePath());
		RosterTools.writeRoster(rosterList,rosterOutputFile);
		
		File drsOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.DRS_FILE));
		logger.info("Writing drs output file: " + rosterOutputFile.getAbsolutePath());
		DrsTools.writeDrs(newDrsList,drsOutputFile);
		
	}


	public int getNextJerseyNumber(List<RosterRaw> rosterList, int teamId){

		List<Integer> jerseyList = rosterList.stream()
		.filter(r -> !r.getName().trim().isEmpty())
		.filter(r -> teamId == r.getTeamId())
		.map(r -> r.getJersey())
		.collect(Collectors.toList());
		
		for(int x = 1; x<=99 ; x++) {
			if(jerseyList.contains(x)) {
				continue;
			}else {
				return x;
			}
		}
		
		throw new RuntimeException("Unable to determine next jersey number");
//		
//		for(RosterRaw rosRaw : rosterList){
//			if(teamId == rosRaw.getTeamId()) {
//				if(!rosRaw.getName().trim().isEmpty()){
////					maxJersey = Integer.max(maxJersey, rosRaw.getJersey());
//					
//				}
//			}
//		}
//		

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
			logger.debug("Unable to add player to roster, no available spots");
			//throw new RuntimeException("Unable to add player to roster, no available spots");	
			this.addMessage("Unable to add player [" + ros.getName() 
			+ "]to roster, no available spots on team [" + Teams.fromId(ros.getTeamId()) + "]");
		}
		
		
	}
	
	private String getInputFilePath(){
		if(inputFile != null){
			return inputFile.getAbsolutePath();
		}
		
		return null;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

}
