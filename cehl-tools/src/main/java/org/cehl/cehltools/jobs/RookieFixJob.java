package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cehl.cehltools.JobType;
import org.cehl.cehltools.dto.TeamPlayerDto;
import org.cehl.commons.SimFileType;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.RosterTools;
import org.cehl.raw.decode.TeamNameProcessor;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.io.Files;

@Component
public class RookieFixJob extends AbstractJob{
	
	private static final Logger logger = Logger.getLogger(RookieFixJob.class);
	
	private File inputFile;

	public RookieFixJob() {
		super(JobType.ROOKIE_FIX);
	
	}
	
	@Override
	public String getJobInfo() {
		return "fix rookie status";
	}

	@Override
	public void _run() {

		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);

		List<TeamPlayerDto> rookieList = getTeamPlayerList();
		
		//reset rookie status on new roster file
		for(TeamPlayerDto rookie : rookieList){
			boolean found = false;
			
			for(RosterRaw rosterRaw : rosterList){
				if(rookie.getTeam().getTeamId() == rosterRaw.getTeamId() && rookie.getPlayerName().equals(rosterRaw.getName())){
					rosterRaw.setVetRookieStatus1(0);
					rosterRaw.setVetRookieStatus2(0);
					
					found = true;
					break;
				}
			}
			
			if(!found){
				for(RosterRaw rosterRaw : rosterList){
					if(rookie.getPlayerName().equals(rosterRaw.getName())){
						rosterRaw.setVetRookieStatus1(0);
						rosterRaw.setVetRookieStatus2(0);
						
						System.out.println("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeam() + " using name only for matching(found on teamId: "+ rosterRaw.getTeamId() +")"  );
						
						found = true;
						continue;
					}
				}
			}
			
			if(!found){
				//throw new RuntimeException("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeamId());
				System.out.println("Cannot find rookie: " + rookie.getPlayerName() + " for team: " + rookie.getTeam());
			}else{
				
			}
		}

		//write files		
		File rosterOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.ROSTER_FILE));
		logger.info("Writing roster output file: " + rosterOutputFile.getAbsolutePath());
		RosterTools.writeRoster(rosterList,rosterOutputFile);


	}
	
	List<TeamPlayerDto> getTeamPlayerList() {
		
		List<TeamPlayerDto> rookieList;
		
		String extension = Files.getFileExtension(inputFile.getName()).toUpperCase();

		if("ROS".equals(extension)) {
			
			rookieList = new ArrayList<TeamPlayerDto>();
			
			List<RosterRaw> rosterPrevSeason = RosterTools.loadRoster(inputFile, true);
			//collect rookie plyers with less than 26 GP
			for(RosterRaw rosterRaw : rosterPrevSeason){
				
				if(rosterRaw.getVetRookieStatus1() == 255){
					if(rosterRaw.getGamesPlayed() >= 25){
						System.out.println(rosterRaw);
						
						rookieList.add(new TeamPlayerDto(Teams.fromId(rosterRaw.getTeamId()), rosterRaw.getName()));
					}
				}
			}
			
		}else if( "CSV".equals(extension)) {
			rookieList = importFromCsv(inputFile);
		}else {
			throw new RuntimeException("Unsupported inputFileType" + inputFile.getName() );
		}
		
		return rookieList;
	}
	

	public static List<TeamPlayerDto> importFromCsv(File file)  {
		
		List<TeamPlayerDto> playerList = new ArrayList<>();

		try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(file),
				CsvPreference.STANDARD_PREFERENCE);){
			

			// the header elements are used to map the values to the bean (names
			// must match)
			String[] header = beanReader.getHeader(true);
			header = new String[] {"Team","PlayerName"};

			final CellProcessor[] processors = getProcessors();
			
			TeamPlayerDto teamPlayer;
			while ((teamPlayer = beanReader.read(TeamPlayerDto.class, header,
					processors)) != null) {
				playerList.add(teamPlayer);
				System.out.println(teamPlayer.toString());
			}

		}catch(SuperCsvConstraintViolationException e){
			throw e;
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		

		return playerList;
	}
	
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new TeamNameProcessor(), // TeamName
				new StrNotNullOrEmpty(new StrMinMax(1, 22, new Trim())), // Prospect Name
				
				
		};

		return processors;
	}


	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}


}
