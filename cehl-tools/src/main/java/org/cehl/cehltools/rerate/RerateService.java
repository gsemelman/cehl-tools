package org.cehl.cehltools.rerate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerRerateDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

public class RerateService {
	
	@Autowired
	PlayerRepository repository;
	
	@Autowired
	DataSource ds;
	
	Map<String,String> nameExceptions = new HashMap<>();
	List<String> dupExceptions = new ArrayList<>();
	
	ItRatingProcessor itProcessor;
	StRatingProcessor stProcessor;
	EnRatingProcessor enProcessor;
	DuRatingProcessor duProcessor; 
	DiRatingProcessor diProcessor;
	PaRatingProcessor paProcessor;
	PcRatingProcessor pcProcessor;
	ScRatingProcessor scProcessor; 
	DfRatingProcessor dfProcessor;
	ExRatingProcessor exProcessor;
	//LdRatingProcessor ldProcessor;
	
	@PostConstruct
    public void init() {
		
		itProcessor = new ItRatingProcessor();
		stProcessor = new StRatingProcessor();
		enProcessor = new EnRatingProcessor();
		duProcessor = new DuRatingProcessor();
		diProcessor = new DiRatingProcessor();
		paProcessor = new PaRatingProcessor();
		pcProcessor= new PcRatingProcessor();
		scProcessor = new ScRatingProcessor();
		dfProcessor= new DfRatingProcessor();
		exProcessor = new ExRatingProcessor();
		//ldProcessor = new LdRatingProcessor();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		nameExceptions = template.query(
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
    }
	
	
	@Transactional
	public RatingResult reratePlayer(PlayerRerateDto rerateDto,int endYear) {

		
		String playerNameSearch = nameExceptions.get(rerateDto.getName()) != null 
				? nameExceptions.get(rerateDto.getName()) : rerateDto.getName();
		
		Player player = null;
		
		try {
			 player = repository.findByName(playerNameSearch);
		}catch(IncorrectResultSizeDataAccessException e) {
			try {
				player = repository.findByNameAndCountry(playerNameSearch, rerateDto.getNationality());
			}catch(IncorrectResultSizeDataAccessException e1) {
				List<Player> players = repository.findPlayersByName(playerNameSearch);
				
				Integer expectedAge = rerateDto.getAge();
				
				for(Player p2 : players) {
					Integer dbAge = Period.between(p2.getDob(), LocalDate.now()).getYears();
					Integer diff = expectedAge - dbAge;
					
					if(p2.getPosition().equals(String.valueOf(rerateDto.getPos()))) {
						player = p2; 
						break;
					}
					
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
		
		if(player == null) {
			return null;
		}
		
		return getRerateResult(player,rerateDto, endYear);
	}
	
	public RatingResult getRerateResult(Player p,PlayerRerateDto rerateDto, int endYear) {
		
		PlayerStatAccumulator psa = new PlayerStatAccumulator(p,endYear);

		double it = itProcessor.getRating(p, psa);
		//double st = stProcessor.getRating(p, psa);
		double st = rerateDto.getSt();
		double sp = rerateDto.getSp();
		double en = enProcessor.getRating(p, psa);
		double du = duProcessor.getRating(p, psa);
		double di = diProcessor.getRating(p,psa);
		double sk = rerateDto.getSk();
		double pa = paProcessor.getRating(p,psa);
		double pc = pcProcessor.getRating(p,psa);
		double df = rerateDto.getDf();
		double sc = scProcessor.getRating(p,psa);
		//double ex = rerateDto.getEx();
		double ex = exProcessor.getSeasonRating(p, psa.getTotalStats());
		double ld = rerateDto.getLd();

		//set inital ov and populate result
		double ov = RerateUtils.calculateOv(p.getPosition(), it, sp, st, 
				en, du, di, sk, pa, pc, df ,sc, ex, ld);
		
		RatingResult rerateResult = new RatingResult(
				it, sp, st,
				en, du, di, sk, pa, pc, df, sc, ex, ld, ov);
		
		//post process
		paProcessor.postProcess(rerateResult);
		
		//set ov after post process
		ov = RerateUtils.calculateOv(p.getPosition(), rerateResult.getIt(), rerateResult.getSp(), rerateResult.getSt(), 
				rerateResult.getEn(), rerateResult.getDu(), rerateResult.getDi(), 
				rerateResult.getSk(), rerateResult.getPa(), rerateResult.getPc(),  
				rerateResult.getDf(),rerateResult.getSc(), rerateResult.getEx(), rerateResult.getLd());
		rerateResult.setOv(ov);

		return rerateResult;
	}

}
