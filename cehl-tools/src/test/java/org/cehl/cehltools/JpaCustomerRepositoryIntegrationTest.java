package org.cehl.cehltools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.cehl.cehltools.rerate.RerateConfig;
import org.cehl.cehltools.rerate.RerateUtils;
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
import org.cehl.cehltools.rerate.processor.ScRatingProcessor;
import org.cehl.cehltools.rerate.processor.StRatingProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = RerateConfig.class)
public class JpaCustomerRepositoryIntegrationTest {
	
	@Autowired
	PlayerRepository repository;
	
	@Autowired
	DataSource ds;
	
	//@Test
	public void test1() {
		//Player player = repository.findById(1379l).orElseGet(null);
		Player player = repository.findByName("Matt Bartkowski");
		
		PlayerStatAccumulator psa = new PlayerStatAccumulator(player);
		//psa.accumulate(2016, 2018);
		//psa.accumulate2();
		
		for(PlayerStatHolder holder : psa.getAllSeasons()) {
			//System.out.println(holder);
		}
		
		//System.out.println(psa.getTotalStats());
		
		//System.out.println(psa.getPreviousSeasonsTotals(5));
		
		//System.out.println(psa.getLastYearStats());
		
		ScRatingProcessor scProcessor = new ScRatingProcessor();
		
		//System.out.println(scProcessor.getRating2(player, psa));
	}
	//1379
	@Test
	public void test() {
		
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
			if(!"Tom Wilson".equalsIgnoreCase(p.getName())){
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
			double du = duProcessor.getSeasonRating(p, totals2);
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
			
			if(gp < 1) {
				return;
			}
			
			double ov = RerateUtils.calculateOv(p.getPosition(), it, sp, st, en, du, di, sk, pa, pc, df, sc, 70, 70);
			
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
					String.valueOf(Precision.round(sc,0) ),
					String.valueOf(Precision.round(df,0) ),
					String.valueOf(Precision.round(ex,0) ),
					String.valueOf(Precision.round(ld,0) ),
					String.valueOf(Precision.round(ov,0) ),
					};

			rows.add(values);

		});
		

		File csvOutputFile = new File("c:/test/ratings.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	


}
