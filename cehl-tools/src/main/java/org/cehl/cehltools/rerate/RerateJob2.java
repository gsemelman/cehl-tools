package org.cehl.cehltools.rerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.cehl.cehltools.CehlUtils;
import org.cehl.cehltools.rerate.dto.PlayerRerateDto;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.commons.SimFileType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RerateJob2 implements IRerateJob {
	
	private static final Logger logger = LoggerFactory.getLogger(RerateJob2.class);

	@Autowired
	RerateService rerateService;
	
	@Autowired
	@Qualifier("simLocation")
	private File simLocation;
	
	@Autowired
	@Qualifier("baseInputLocation")
	private File baseInputLocation;
	
	@Autowired
	@Qualifier("baseOutputLocation")
	private File baseOutputLocation;
	
	@Autowired
	@Qualifier("leaguePrefix")
	private String leaguePrefix;
	

	public void runJob(int endYear) {
	
		List<RosterRaw> rosterList = RosterTools.loadRoster(
				CehlUtils.getLeagueFileByType(simLocation, leaguePrefix, SimFileType.ROSTER_FILE), true); //skip blanks

		rosterList.forEach(ros -> {
			
//			if(!"Andrei Markov".equalsIgnoreCase(ros.getName())){
//				return;
//			}
			
			PlayerRerateDto rerateDto = new PlayerRerateDto(ros.getName(),ros.getAge(),ros.getBirthPlace());
			PlayerPositionType position = PlayerPositionType.PositionByRawValue(ros.getPosition());
			rerateDto.setPos(position.stringValue().charAt(0));
			
			RatingResult result = rerateService.reratePlayer(rerateDto, endYear);
			
			if(result != null) {
				ros.setIt((int) result.getIt());
				ros.setEn((int) result.getEn());
				ros.setDu((int) result.getDu());
				ros.setDi((int) result.getDi());
				ros.setPa((int) result.getPa());
				ros.setPc((int) result.getPc());
				ros.setSc((int) result.getSc());
				ros.setEx((int) result.getEx());
				ros.setLd((int) result.getLd());
//				if(position.isForward()) {
//					ros.setDf((int) result.getDf());
//				}
				ros.setFiller1((int) rerateDto.getSt());
				ros.setFiller3((int) rerateDto.getSk());
			}
		});

		
		output(rosterList);
	}
	
	public void output(List<RosterRaw> rosterList) {

		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Team","Name","#","Age","POS","IT","SP","ST","EN","DU","DI","SK","PA","PC","DF","SC","EX","LD","OV","Seasons","Played Last Season"});

		
		rosterList.forEach(ros -> {

			PlayerPositionType position = PlayerPositionType.PositionByRawValue(ros.getPosition());
			
			//skip goalies
			if(PlayerPositionType.GOALIE.equals(position)) return;

			double ov = RerateUtils.calculateOv(position.stringValue(), ros.getIt(), ros.getSp(), ros.getSt(), 
					ros.getEn(), ros.getDu(), ros.getDi(), ros.getSk(),
					ros.getPa(), ros.getPc(), ros.getDf() ,ros.getSc(), ros.getEx(), ros.getLd());
			
			String[] values = new String[] {
					ros.getTeamName(),
					ros.getName(), 
					String.valueOf(ros.getJersey()), 
					String.valueOf(ros.getAge()),
					position.stringValue(), 
					String.valueOf(ros.getIt() ), 
					String.valueOf(ros.getSp() ), 
					String.valueOf(ros.getSt() ), 
					String.valueOf(ros.getEn() ), 
					String.valueOf(ros.getDu()), 
					String.valueOf(ros.getDi()), 
					String.valueOf(ros.getSk()), 
					String.valueOf(ros.getPa()), 
					String.valueOf(ros.getPc() ), 
					String.valueOf(ros.getDf() ),
					String.valueOf(ros.getSc()),
					String.valueOf(ros.getEx()),
					String.valueOf(ros.getLd() ),
					String.valueOf(ov),
					String.valueOf(ros.getFiller1()),
					(ros.getFiller3() == 1 ? "Y" : "N")
					
					};

			rows.add(values);
			
		});

		File csvOutputFile = new File("output/ratings.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
}
