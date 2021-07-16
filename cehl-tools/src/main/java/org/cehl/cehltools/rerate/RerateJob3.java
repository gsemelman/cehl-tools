package org.cehl.cehltools.rerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cehl.cehltools.CehlUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerService;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.commons.SimFileType;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.DecodeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RerateJob3 implements IRerateJob {
	
	private static final Logger logger = LoggerFactory.getLogger(RerateJob3.class);
	
	private static int RECORD_LENGTH = 22;
	private static int TEAM_LENGTH = 2700; //record length is 2700 but team can only fit 100 prospects

	@Autowired
	RerateService rerateService;
	
	@Autowired
	PlayerService repository;
	
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
	
	private int gameThreshold = 1;

	public void runJob(int endYear) {
	
		List<byte[]> byteList = DecodeTools.decodeFile(
				CehlUtils.getLeagueFileByType(simLocation, leaguePrefix, SimFileType.PROSPECT_FILE),
				TEAM_LENGTH);

		List<ProspectRerateDto> prospectList = new ArrayList<>();
		
		int teamId = 0;
		for(byte[] bytes: byteList) {
			for(int x = 0; x < 100 ; x++) { //only supports 100 prospects in the file so we can ignore the other filler bytes

				int from = x * RECORD_LENGTH;
				int to = from + RECORD_LENGTH;
				byte[] rawRecord = Arrays.copyOfRange(bytes, from, to);

				String prospectName = DecodeTools.readString(rawRecord);

				if(StringUtils.isNoneEmpty(prospectName)) {
					
					List<Player> players = repository.findPlayersByName(prospectName);
					
					if(!players.isEmpty()) {
						for(Player player : players) {
							PlayerStatAccumulator psa = new PlayerStatAccumulator(player,endYear);
							
							if(psa.getTotalStats().getGp() >= gameThreshold) {
//								ProspectRerateDto dto = new ProspectRerateDto();
//								CehlTeam team = CehlTeam.fromId(teamId);
//								dto.setTeamName(team.getName());
//								dto.setProspectName(DecodeTools.readString(rawRecord));
//								dto.setAge(player.getAge());
//								dto.setNationality(player.getCountry());
//								dto.setHeight(player.getHeight());
//								dto.setWeight(player.getWeight());
//								switch(player.getPosition()) {
//								case "L":
//									dto.setPosition("LW");
//									break;
//								case "R":
//									dto.setPosition("RW");
//									break;
//								default:
//									dto.setPosition(player.getPosition());
//									break;
//								}
								
								ProspectRerateDto dto = new ProspectRerateDto();
								CehlTeam team = CehlTeam.fromId(teamId);
								dto.setTeam(team);
								RatingResult result = rerateService.reratePlayer(player, endYear);
								dto.setResult(result);

								prospectList.add(dto);
							}
						}
					}
				
				}


			}

			teamId++;

		}
		
		File csvOutputFile = new File("output/prospects.csv");
		output(prospectList, csvOutputFile);
	}
	
	public void output(List<ProspectRerateDto> rerateDtos, File csvOutputFile) {

		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Team","Name","Age","Height","Weight","Nat","POS","IT","SP","ST","EN","DU","DI","SK","PA","PC","DF","SC","EX","LD","OV"});
		
		rerateDtos.forEach(dto -> {

			RatingResult ros = dto.getResult();
			
			String position = ros.getPlayer().getPosition();
			
			switch(position) {
			case "L":
				position = "LW";
				break;
			case "R":
				position = "RW";
				break;
			}

			double ov = RerateUtils.calculateOv(position, ros.getIt(), ros.getSp(), ros.getSt(), 
					ros.getEn(), ros.getDu(), ros.getDi(), ros.getSk(),
					ros.getPa(), ros.getPc(), ros.getDf() ,ros.getSc(), ros.getEx(), ros.getLd());
			
			String[] values = new String[] {
					dto.getTeam().getName(),
					dto.getResult().getPlayer().getName(), 
					String.valueOf(dto.getResult().getPlayer().getAge()),
					String.valueOf(dto.getResult().getPlayer().getHeight()),
					String.valueOf(dto.getResult().getPlayer().getWeight()),
					dto.getResult().getPlayer().getCountry(),
					position,
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
					String.valueOf(ov)};
			rows.add(values);
			
		});


		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

//	public static void writeFile(List<ProspectRerateDto> prospectList, File outputFile) throws IOException{
//
//	     ICsvBeanWriter beanWriter = null;
//	        try {
//	                beanWriter = new CsvBeanWriter(new FileWriter(outputFile),
//	                        CsvPreference.STANDARD_PREFERENCE);
//	                
//	                // the header elements are used to map the bean values to each column (names must match)
//	                final String[] header = new String[] { "TeamName", "ProspectName","Age","Height","Weight","Nationality","Position" };
//
//	                final CellProcessor[] processors = new CellProcessor[] { 
//	                        new NotNull(), // team name
//	                        new NotNull(), // prosepct name
//	                        new NotNull(), // age
//	                        new NotNull(), // height
//	                        new NotNull(), // weight
//	                        new NotNull(), // nat
//	                        new NotNull(), // position
//	                };
//	                
//	                
//	                // write the header
//	                beanWriter.writeHeader(header);
//	                
//	                // write the beans
//	                for( final ProspectRerateDto dto : prospectList ) {
//	                        beanWriter.write(dto, header, processors);
//	                }
//	                
//	        }
//	        finally {
//	                if( beanWriter != null ) {
//	                        beanWriter.close();
//	                }
//	        }
//		
//
//	
//		
//	}
	
	public class ProspectRerateDto{
		RatingResult result;
		CehlTeam team;
		public RatingResult getResult() {
			return result;
		}
		public void setResult(RatingResult result) {
			this.result = result;
		}
		public CehlTeam getTeam() {
			return team;
		}
		public void setTeam(CehlTeam team) {
			this.team = team;
		}
		
		
	}
	
}
