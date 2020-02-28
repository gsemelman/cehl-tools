package org.cehl.cehltools.rerate.agg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;

public class PlayerStatAccumulator {
	

	private final Player player;
	private final Integer startYear;
	private final Integer endYear;
	
	private Integer lastYearPlayed;
	private int totalSeasons;
	private List<Integer> yearsPlayed;
	
	private PlayerStatHolder totalStats;
	private Map<Integer,PlayerStatHolder> statsByYear;
	
	public PlayerStatAccumulator(Player player) {
		this(player, null, null);
	}
	
	public PlayerStatAccumulator(Player player, Integer endYear) {
		this(player, null, endYear);
	}
	
	public PlayerStatAccumulator(Player player, Integer startYear, Integer endYear) {
		this.player = player;
		this.startYear = startYear;
		this.endYear = endYear;
		
		this.statsByYear= new HashMap<>();
		
		accumulate();
	}

	
	public void accumulate() {

		yearsPlayed = player.getSeasons().stream()
				.map(PlayerSeason::getYear)
				.filter(year-> {
					if(startYear !=null && endYear != null) {
						return year >= startYear && year <= endYear;
					}else if(startYear !=null ) {
						return year >= startYear;
					}else if(endYear !=null ) {
						return year <= endYear;				
					}
					return true;
				})
				.collect(Collectors.toList());
		
		yearsPlayed.sort(Comparator.reverseOrder());
		
		if(yearsPlayed.size() > 0) {
			lastYearPlayed = yearsPlayed.get(0);
			
			for(Integer year : yearsPlayed) {
				PlayerSeason season = player.getSeasonByYear(year);
				
				PlayerStatHolder holder = new PlayerStatHolder();
			
				holder.setYear(year);
				
				if(season.getStatsAll() != null) {
					holder.setGp(season.getStatsAll().getGp());
					holder.setToi(season.getStatsAll().getToi());
					
					holder.setGoals(season.getStatsAll().getGoals());
					holder.setAssists(season.getStatsAll().getAssists());
					
					holder.setPim(season.getStatsAll().getPim());
					holder.setPenMinor(season.getStatsAll().getPenMinor());
					holder.setPenMajor(season.getStatsAll().getPenMajor());
					holder.setPenMisconduct(season.getStatsAll().getPenMisconduct());
					
					holder.setGiveAway(season.getStatsAll().getGiveAway());
					holder.setTakeAway(season.getStatsAll().getTakeAway());
					holder.setShotsBlocked(season.getStatsAll().getShotsBlocked());
					holder.setRushAttempt(season.getStatsAll().getRushAttempt());
					
					holder.setHits(season.getStatsAll().getHits());
				}
	
				if(season.getStatsOnIce() != null) {
					holder.setCf(season.getStatsOnIce().getCf());
					holder.setCa(season.getStatsOnIce().getCa());
					holder.setCfPct(season.getStatsOnIce().getCfPct());
					
					holder.setFf(season.getStatsOnIce().getFf());
					holder.setFa(season.getStatsOnIce().getFa());
					holder.setFfPct(season.getStatsOnIce().getFfPct());
					
					holder.setoZoneStart(season.getStatsOnIce().getoZoneStart());
					holder.setdZoneStart(season.getStatsOnIce().getdZoneStart());
					holder.setnZoneStart(season.getStatsOnIce().getnZoneStart());
				}
				
				if(season.getStatsPp() != null) {
					holder.setPpToi(season.getStatsPp().getToi());
					holder.setPpGoals(season.getStatsPp().getGoals());
					holder.setPpAssists(season.getStatsPp().getAssists());
					holder.setPpPoints(season.getStatsPp().getPoints());
				}
				
				if(season.getStatsPk() != null) {
					holder.setPkToi(season.getStatsPk().getToi());
				}

				statsByYear.put(year, holder);
			}
		}
		
		totalSeasons = statsByYear.size();

		totalStats = accumulateTotals(statsByYear.values());
		totalStats.setYear(totalSeasons);
	
	}
	
	public PlayerStatHolder accumulateTotals(Collection<PlayerStatHolder> seasons) {
		
		PlayerStatHolder totals = new PlayerStatHolder();
		
		totals.setGp(seasons.stream().mapToInt(PlayerStatHolder::getGp).sum());
		totals.setToi(seasons.stream().mapToInt(PlayerStatHolder::getToi).sum());
		totals.setGoals(seasons.stream().mapToInt(PlayerStatHolder::getGoals).sum());
		totals.setAssists(seasons.stream().mapToInt(PlayerStatHolder::getAssists).sum());
		
		totals.setPim(seasons.stream().mapToInt(PlayerStatHolder::getPim).sum());
		totals.setPenMinor(seasons.stream().mapToInt(PlayerStatHolder::getPenMinor).sum());
		totals.setPenMajor(seasons.stream().mapToInt(PlayerStatHolder::getPenMajor).sum());
		totals.setPenMisconduct(seasons.stream().mapToInt(PlayerStatHolder::getPenMisconduct).sum());
		
		totals.setGiveAway(seasons.stream().mapToInt(PlayerStatHolder::getGiveAway).sum());
		totals.setTakeAway(seasons.stream().mapToInt(PlayerStatHolder::getTakeAway).sum());
		totals.setShotsBlocked(seasons.stream().mapToInt(PlayerStatHolder::getShotsBlocked).sum());
		totals.setRushAttempt(seasons.stream().mapToInt(PlayerStatHolder::getRushAttempt).sum());
		
		totals.setHits(seasons.stream().mapToInt(PlayerStatHolder::getHits).sum());
		
		totals.setCf(seasons.stream().mapToInt(PlayerStatHolder::getHits).sum());
		totals.setCa(seasons.stream().mapToInt(PlayerStatHolder::getHits).sum());
		totals.setCfPct(seasons.stream().mapToDouble(PlayerStatHolder::getCfPct).average().orElse(0));
		
		totals.setFf(seasons.stream().mapToInt(PlayerStatHolder::getFf).sum());
		totals.setFa(seasons.stream().mapToInt(PlayerStatHolder::getFa).sum());
		totals.setFfPct(seasons.stream().mapToDouble(PlayerStatHolder::getFfPct).average().orElse(0));
		
		totals.setoZoneStart(seasons.stream().mapToInt(PlayerStatHolder::getoZoneStart).sum());
		totals.setdZoneStart(seasons.stream().mapToInt(PlayerStatHolder::getdZoneStart).sum());
		totals.setnZoneStart(seasons.stream().mapToInt(PlayerStatHolder::getnZoneStart).sum());
		
		totals.setPpToi(seasons.stream().mapToInt(PlayerStatHolder::getPpToi).sum());
		totals.setPpGoals(seasons.stream().mapToInt(PlayerStatHolder::getPpGoals).sum());
		totals.setPpAssists(seasons.stream().mapToInt(PlayerStatHolder::getPpAssists).sum());
		totals.setPpPoints(seasons.stream().mapToInt(PlayerStatHolder::getPpPoints).sum());
		
		totals.setPkToi(seasons.stream().mapToInt(PlayerStatHolder::getPkToi).sum());

		

		
		return totals;
	}
	
	public PlayerStatHolder getPreviousSeasonsTotals(int seasons) {

		List<PlayerStatHolder> prevSeasons = getPreviousSeasonsStats(seasons);
		
		return accumulateTotals(prevSeasons);
	}
//	
//	public PlayerStatHolder getPreviousSeasonTotalsFrom(int fromYear) {
//
//		List<PlayerStatHolder> prevSeasons = new ArrayList<>();
//		
//		List<Integer> years = new ArrayList<>(statsByYear.keySet());
//		years.sort(Comparator.reverseOrder());
//		
//
//		for(Integer year : years) {
//			if(year <= fromYear) {
//				if(statsByYear.containsKey(year)) {
//					prevSeasons.add(statsByYear.get(year));
//				}
//			}
//		}
//		
//		return accumulateTotals(prevSeasons);
//	}
//	

	
	public List<PlayerStatHolder> getPreviousSeasonsStats(int seasons) {
		
		if(seasons < 1) throw new IllegalArgumentException("cannot be lower than 1");

		List<PlayerStatHolder> list = new ArrayList<>();
		
		List<Integer> years = new ArrayList<>(statsByYear.keySet());
		years.sort(Comparator.reverseOrder());
		
		int count = 1;
		for(Integer year : years) {
			list.add(statsByYear.get(year));
			
			if(count >= seasons) break;
			count++;
		}

		return list;
	}
	
	public PlayerStatHolder getLastYearStats() {

		if(lastYearPlayed == null) return null;
		
		return statsByYear.get(lastYearPlayed);
	}
	

	public PlayerStatHolder getStatsByYear(int year) {
		return statsByYear.get(year);
	}
	
	public List<PlayerStatHolder> getAllSeasons(){
		
		List<PlayerStatHolder> allSeasons = new ArrayList<>(statsByYear.values());
		Collections.sort(allSeasons, new Comparator<PlayerStatHolder>() {

			@Override
			public int compare(PlayerStatHolder o1, PlayerStatHolder o2) {
				
				if(o1.getYear() == o2.getYear()) return 0;
				
				return (o1.getYear() - o2.getYear() > 0 ? -1 : 1)  ;
			}});
		
		return allSeasons;
	}

	public PlayerStatHolder getTotalStats() {
		return totalStats;
	}


	public int getTotalSeasons() {
		return totalSeasons;
	}

	public Integer getLastYearPlayed() {
		return lastYearPlayed;
	}
	
	
	
}
