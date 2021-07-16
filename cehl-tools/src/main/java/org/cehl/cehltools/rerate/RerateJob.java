package org.cehl.cehltools.rerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerRepository;
import org.cehl.cehltools.rerate.processor.DfRatingProcessor;
import org.cehl.cehltools.rerate.processor.DiRatingProcessor;
import org.cehl.cehltools.rerate.processor.DuRatingProcessor;
import org.cehl.cehltools.rerate.processor.EnRatingProcessor;
import org.cehl.cehltools.rerate.processor.ExRatingProcessor;
import org.cehl.cehltools.rerate.processor.ItRatingProcessor;
import org.cehl.cehltools.rerate.processor.PaRatingProcessor;
import org.cehl.cehltools.rerate.processor.PcRatingProcessor;
import org.cehl.cehltools.rerate.processor.ScRatingProcessor;
import org.cehl.cehltools.rerate.processor.StRatingProcessor;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.commons.SimFileType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

public class RerateJob {
	
	private static final Logger logger = LoggerFactory.getLogger(RerateJob.class);
	@Autowired
	PlayerRepository repository;
	
	@Autowired
	DataSource ds;
	
	
	@Autowired
	@Qualifier("simLocation")
	private File simLocation;
	
	@Autowired
	@Qualifier("backupLocation")
	private File backupLocation;
	
	@Autowired
	@Qualifier("baseInputLocation")
	private File baseInputLocation;
	
	@Autowired
	@Qualifier("baseOutputLocation")
	private File baseOutputLocation;
	
	@Autowired
	@Qualifier("leaguePrefix")
	private String leaguePrefix;
	


	@Transactional
	public void reratePlayers(int endYear) {
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		List<String> dupExceptions = new ArrayList<>();
		Map<String,String> nameExceptions = template.query(
				"SELECT simName, linkName, duplicate FROM player_ratings.player_exception", 
				new ResultSetExtractor<Map<String,String>>(){
		    @Override
		    public Map<String,String> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,String> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("simName"),rs.getString("linkName"));
		            
		            if(rs.getBoolean("duplicate")) {
		            	dupExceptions.add(rs.getString("linkName"));
		            }
		        }
		        return mapRet;
		    }
		});

		List<RosterRaw> rosterList = RosterTools.loadRoster(getLeagueFileByType(SimFileType.ROSTER_FILE), false);

		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Team","Number","Name","Position","IT","SP","ST","EN","DU","DI","SK","PA","PC","DF","SC","EX","LD","OV"});


		for(RosterRaw rosterRaw : rosterList){
			
			if(!"Auston Matthews".equalsIgnoreCase(rosterRaw.getName())){
				//continue;
			}
			
			PlayerPositionType position = PlayerPositionType.PositionByRawValue(rosterRaw.getPosition());
			CehlTeam team = CehlTeam.fromId(rosterRaw.getTeamId());
			
			if(StringUtils.isBlank(rosterRaw.getName())) continue;
			
			double ov = RerateUtils.calculateOv(position.stringValue(), 
					rosterRaw.getIt(), rosterRaw.getSp(), rosterRaw.getSt(),
					rosterRaw.getEn(), rosterRaw.getDu(), rosterRaw.getDi(),
					rosterRaw.getSk(), rosterRaw.getPa(), rosterRaw.getPc(), 
					rosterRaw.getDf(), rosterRaw.getSc(), rosterRaw.getEx(), rosterRaw.getLd());
			
			String[] initialValues = new String[] {
					team.getName(),
					String.valueOf(rosterRaw.getJersey()), 
					rosterRaw.getName(), 
					position.stringValue(),
					String.valueOf(rosterRaw.getIt()), 
					String.valueOf(rosterRaw.getSp()), 
					String.valueOf(rosterRaw.getSt()), 
					String.valueOf(rosterRaw.getEn()), 
					String.valueOf(rosterRaw.getDu()), 
					String.valueOf(rosterRaw.getDi()), 
					String.valueOf(rosterRaw.getSk()), 
					String.valueOf(rosterRaw.getPa()), 
					String.valueOf(rosterRaw.getPc()), 
					String.valueOf(rosterRaw.getDf()), 
					String.valueOf(rosterRaw.getSc()), 
					String.valueOf(rosterRaw.getEx()), 
					String.valueOf(rosterRaw.getLd()), 
					String.valueOf(ov),
					};
			
			rows.add(initialValues);

			//Player player = players.get(rosterRaw.getName().toUpperCase());
			
			String playerNameSearch = nameExceptions.get(rosterRaw.getName()) != null 
					? nameExceptions.get(rosterRaw.getName()) : rosterRaw.getName();
			
			Player player = null;
			
			try {
				 player = repository.findByName(playerNameSearch);
			}catch(IncorrectResultSizeDataAccessException e) {
				try {
					player = repository.findByNameAndCountry(playerNameSearch, rosterRaw.getBirthPlace());
				}catch(IncorrectResultSizeDataAccessException e1) {
					List<Player> players = repository.findPlayersByName(playerNameSearch);
					
					Integer expectedAge = rosterRaw.getAge();
					
					for(Player p2 : players) {
						Integer dbAge = Period.between(p2.getDob(), LocalDate.now()).getYears();
						Integer diff = expectedAge - dbAge;
						
						if(dbAge.equals(expectedAge)) {
							player = p2;
							break;
						}
						
						if(Math.abs(diff) == 1) {
							player = p2;
							break;
						}

					}
					
					if(player == null) {
						//player = players.get(0);
						throw new RuntimeException("Unable to find unique player record for name:" + playerNameSearch);
					}
				}
			}
			
//			if(player!= null && playerException.containsKey(player.getName().toUpperCase() + "-" + player.getCountry())) {
//				player = playerException.get(player.getName().toUpperCase() + "-" + player.getCountry());
//			}
			
			if(player != null) {
				String[] rerateValues = getRerateValues(player, rosterRaw, endYear);
				rows.add(append(rerateValues, "FOUND"));
			}else {
				rows.add(append(initialValues,"NOT FOUND"));
			}
			
			rows.add(new String[] {});
		}


		File csvOutputFile = new File("c:/test/ratings.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public String[] getRerateValues(Player p, RosterRaw rosterRaw, int endYear) {

		ItRatingProcessor itProcessor = new ItRatingProcessor();
		StRatingProcessor stProcessor = new StRatingProcessor();
		EnRatingProcessor enProcessor = new EnRatingProcessor();
		DuRatingProcessor duProcessor = new DuRatingProcessor();
		DiRatingProcessor diProcessor = new DiRatingProcessor();
		PaRatingProcessor paProcessor = new PaRatingProcessor();
		PcRatingProcessor pcProcessor= new PcRatingProcessor();
		ScRatingProcessor scProcessor = new ScRatingProcessor();
		DfRatingProcessor dfProcessor= new DfRatingProcessor();
		ExRatingProcessor exProcessor = new ExRatingProcessor();
		
		PlayerStatAccumulator psa = new PlayerStatAccumulator(p,endYear);
		PlayerStatHolder totals2 = psa.getTotalStats();


		double it = itProcessor.getRating(p, psa);
		//double st = stProcessor.getSeasonRating(p, totals);
		//double st = stProcessor.getRating(p, psa);
		double en = enProcessor.getRating(p, psa);
		double du = duProcessor.getRating(p, psa);
		double di = diProcessor.getRating(p,psa);
		double pa = paProcessor.getRating(p,psa);
		double pc = pcProcessor.getRating(p,psa);
		double sc = scProcessor.getRating(p,psa);
		double ex = exProcessor.getSeasonRating(p, totals2);
		double ld = exProcessor.getSeasonRating(p, totals2);

		//set inital ov and populate result
		double ov = RerateUtils.calculateOv(p.getPosition(), it, rosterRaw.getSp(), rosterRaw.getSt(), 
				en, du, di, rosterRaw.getSk(), pa, pc,  rosterRaw.getDf(),sc, rosterRaw.getEx(), rosterRaw.getLd());
		
		RatingResult rerateResult = new RatingResult(
				it, rosterRaw.getSp(), rosterRaw.getSt(),
				en, du, di, rosterRaw.getSk(), pa, pc, rosterRaw.getDf(), sc, rosterRaw.getEx(), rosterRaw.getLd(), ov, p);
		
		//post process
		paProcessor.postProcess(rerateResult);
		
		//set ov after post process
		ov = RerateUtils.calculateOv(p.getPosition(), rerateResult.getIt(), rerateResult.getSp(), rerateResult.getSt(), 
				rerateResult.getEn(), rerateResult.getDu(), rerateResult.getDi(), 
				rosterRaw.getSk(), rerateResult.getPa(), rerateResult.getPc(),  
				rerateResult.getDf(),rerateResult.getSc(), rerateResult.getEx(), rerateResult.getLd());
		
		rerateResult.setOv(ov);
		
		
		PlayerPositionType position = PlayerPositionType.PositionByRawValue(rosterRaw.getPosition());
		
		String[] values = new String[] {
				rosterRaw.getTeamName(),
				String.valueOf(rosterRaw.getJersey()), 
				rosterRaw.getName(), 
				position.stringValue(),
				String.valueOf(Precision.round(rerateResult.getIt(),0) ), 
				String.valueOf(Precision.round(rerateResult.getSp(),0) ), 
				String.valueOf(Precision.round(rerateResult.getSt(),0) ), 
				String.valueOf(Precision.round(rerateResult.getEn(),0) ), 
				String.valueOf(Precision.round(rerateResult.getDu(),0) ), 
				String.valueOf(Precision.round(rerateResult.getDi(),0) ), 
				String.valueOf(Precision.round(rerateResult.getSk(),0) ), 
				String.valueOf(Precision.round(rerateResult.getPa(),0) ), 
				String.valueOf(Precision.round(rerateResult.getPc(),0) ), 
				String.valueOf(Precision.round(rerateResult.getDf(),0) ),
				String.valueOf(Precision.round(rerateResult.getSc(),0) ),
				String.valueOf(Precision.round(rerateResult.getEx(),0) ),
				String.valueOf(Precision.round(rerateResult.getLd(),0) ),
				String.valueOf(Precision.round(rerateResult.getOv(),0) ),
				};

		return values;
	}

	@Transactional
	public void test2() {
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		Map<String,Integer> spMap = template.query(
				"select case WHEN e.simName IS NULL THEN m.name ELSE e.linkName END as name, sp from player_ratings.cehl_master m "
				+ " LEFT OUTER JOIN player_ratings.player_exception e ON m.name = e.simName", 
				new ResultSetExtractor<Map<String,Integer>>(){
		    @Override
		    public Map<String,Integer> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Integer> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("name"),rs.getInt("sp"));
		        }
		        return mapRet;
		    }
		});
		
		Map<String,Integer> skMap = template.query("select case WHEN e.simName IS NULL THEN m.name ELSE e.linkName END as name, sk from player_ratings.cehl_master m "
				+ " LEFT OUTER JOIN player_ratings.player_exception e ON m.name = e.simName", 
				new ResultSetExtractor<Map<String,Integer>>(){
		    @Override
		    public Map<String,Integer> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Integer> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("name"),rs.getInt("sk"));
		        }
		        return mapRet;
		    }
		});
		
		Map<String,Integer> dfMap = template.query("select case WHEN e.simName IS NULL THEN m.name ELSE e.linkName END as name, df from player_ratings.cehl_master m "
				+ " LEFT OUTER JOIN player_ratings.player_exception e ON m.name = e.simName", 
				new ResultSetExtractor<Map<String,Integer>>(){
		    @Override
		    public Map<String,Integer> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Integer> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("name"),rs.getInt("df"));
		        }
		        return mapRet;
		    }
		});
		
		Map<String,Integer> stMap = template.query("select case WHEN e.simName IS NULL THEN m.name ELSE e.linkName END as name, st from player_ratings.cehl_master m "
				+ " LEFT OUTER JOIN player_ratings.player_exception e ON m.name = e.simName", 
				new ResultSetExtractor<Map<String,Integer>>(){
		    @Override
		    public Map<String,Integer> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Integer> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("name"),rs.getInt("st"));
		        }
		        return mapRet;
		    }
		});
		
		Iterable<Player> playeritr = repository.findAll(Sort.by("name").ascending());
		

		ItRatingProcessor itProcessor = new ItRatingProcessor();
		StRatingProcessor stProcessor = new StRatingProcessor();
		EnRatingProcessor enProcessor = new EnRatingProcessor();
		DuRatingProcessor duProcessor = new DuRatingProcessor();
		DiRatingProcessor diProcessor = new DiRatingProcessor();
		PaRatingProcessor paProcessor = new PaRatingProcessor();
		PcRatingProcessor pcProcessor= new PcRatingProcessor();
		ScRatingProcessor scProcessor = new ScRatingProcessor();
		DfRatingProcessor dfProcessor= new DfRatingProcessor();
		ExRatingProcessor exProcessor = new ExRatingProcessor();

		
		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Name","POS","Seasons","Last Year","GP_TOTAL","GP_LAST3","G","GPG","A","IT","SP","ST","EN","DU","DI","SK","PA","PC","SC","DF","ED","LD","OV"});
		
		
		playeritr.forEach(p-> {
			
//			if(!"Alex Ovechkin".equalsIgnoreCase(p.getName())){
//				return;
//			}
			//if(!"Mikhail Sergachev".equalsIgnoreCase(p.getName())){
			if(!"Brent Burns".equalsIgnoreCase(p.getName())){
			// return;
			}
			
			PlayerStatAccumulator psa = new PlayerStatAccumulator(p);
			PlayerStatHolder totals = psa.getPreviousSeasonsTotals(3); //last seasons
			//PlayerStatHolder totals2 = psa.getPreviousSeasonsTotals(20); //last 10 seasons
			PlayerStatHolder totals2 = psa.getTotalStats();

			if(psa.getLastYearPlayed() < 2017) return;
			
	
			int seasons = psa.getPreviousSeasonsStats(3).size();
			
			if(seasons == 0) return;
			
			double lastYearPlayed = psa.getLastYearPlayed();

			double gp = totals.getGp();
			double goals = totals.getGoals();
			double assists = totals.getAssists();
			
			double sp = 70;
			double sk = 70;
			
			if(spMap.containsKey(p.getName())) {
				sp = spMap.get(p.getName());
			}
			if(skMap.containsKey(p.getName())) {
				sk = skMap.get(p.getName());
			}
	

			double it = itProcessor.getRating(p, psa);
			//double st = stProcessor.getSeasonRating(p, totals);
			double st = stProcessor.getRating(p, psa);
			double en = enProcessor.getRating(p, psa);
			double du = duProcessor.getRating(p, psa);
			double di = diProcessor.getRating(p,psa);
			double pa = paProcessor.getRating(p,psa);
			double pc = pcProcessor.getRating(p,psa);
			double sc = scProcessor.getRating(p,psa);
			double df = dfProcessor.getRating(p,psa);
			double ex = exProcessor.getSeasonRating(p, totals2);
			double ld = exProcessor.getSeasonRating(p, totals2);
			
			df = 70;
			
			if(dfMap.containsKey(p.getName())) {
				df = dfMap.get(p.getName());
			}
			
			if(stMap.containsKey(p.getName())) {
				st = stMap.get(p.getName());
			}
			
			if(gp < 1) {
				return;
			}
			
			double ov = RerateUtils.calculateOv(p.getPosition(), it, sp, st, en, du, di, sk, pa, pc, df, sc, ex, ld);
			
			String[] values = new String[] {p.getName(), 
					String.valueOf(p.getPosition().charAt(0)), 
					String.valueOf(seasons), 
					String.valueOf(lastYearPlayed),
					String.valueOf(totals2.getGp()), 
					String.valueOf(gp),
					String.valueOf(goals), 
					String.valueOf(goals / gp),
					String.valueOf(assists), 
					String.valueOf(Precision.round(it,0) ), 
					String.valueOf(Precision.round(sp,0) ), 
					String.valueOf(Precision.round(st,0) ), 
					String.valueOf(Precision.round(en,0) ), 
					String.valueOf(Precision.round(du,0) ), 
					String.valueOf(Precision.round(di,0) ), 
					String.valueOf(Precision.round(sk,0) ), 
					String.valueOf(Precision.round(pa,0) ), 
					String.valueOf(Precision.round(pc,0) ), 
					String.valueOf(Precision.round(df,0) ),
					String.valueOf(Precision.round(sc,0) ),
					String.valueOf(Precision.round(ex,0) ),
					String.valueOf(Precision.round(ld,0) ),
					String.valueOf(Precision.round(ov,0) ),
					};

			rows.add(values);

		});
		

		File csvOutputFile = new File("c:/test/ratings2.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public File[] getLeagueFiles(){
		File[] leagueFiles = simLocation.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().startsWith(leaguePrefix.toLowerCase() + ".");
		    }
		});

		return leagueFiles;
	}
	
	public File getLeagueFileByType(SimFileType fileType){
		for(File file : getLeagueFiles()){
			if(file.getName().endsWith(fileType.getExtension())){
				return file;
			}
		}
		
		return null;
	}
	
	static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
}
