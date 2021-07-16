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

import com.google.common.base.Throwables;

public class PlayerStatAccumulator {
	

	private final Player player;
	private Integer startYear;
	private Integer endYear;
	
	private Integer lastYearPlayed;
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
		
		//determine stat and end years if not set..
		//can only perform this action if at least one season exists.
		if(player.getSeasons().size() > 0) {
			if(this.startYear == null) {
				
				this.startYear = player.getSeasons().stream()
				.map(PlayerSeason::getYear)
				.min(Integer::compare).get();
			}
			
			if(this.endYear == null) {
				this.endYear = player.getSeasons().stream()
				.map(PlayerSeason::getYear)
				.max(Integer::compare).get();
			}
		}else {
			startYear = 0;
			endYear = 0;
		}
		
		//preprocess season results (incomplete seasons etc)
		player.getSeasons().forEach(s->preProcess(s));
		
//		if(endYear > startYear) {
//			throw new RuntimeException("start year cannot be after end year");
//		}
		
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
				
				if(season.getStatsOnIce() != null) {
					holder.setXgf(season.getStatsOnIce().getXgf());
					holder.setXga(season.getStatsOnIce().getXga());
					holder.setXgfPct(season.getStatsOnIce().getXgfPct());
				}

				statsByYear.put(year, holder);
			}
		}
		
		totalStats = accumulateTotals(statsByYear.values());
		//totalStats.setYear(statsByYear.size()); //TODO move this somewhere else
	
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
		
		totals.setYear(seasons.size()); //TODO fix this
		
		totals.setXgf(seasons.stream().mapToInt(PlayerStatHolder::getXgf).sum());
		totals.setXga(seasons.stream().mapToInt(PlayerStatHolder::getXga).sum());
		totals.setXgfPct(seasons.stream().mapToDouble(PlayerStatHolder::getXgfPct).average().orElse(0));
		return totals;
	}
	
	public PlayerStatHolder getPreviousSeasonsTotals(int seasons) {

		List<PlayerStatHolder> prevSeasons = getPreviousSeasonsStats(seasons);
		
		return accumulateTotals(prevSeasons);
	}
	
	public PlayerStatHolder getPreviousSeasonTotalsFromYear(int seasons, int endYear) {
		List<PlayerStatHolder> prevSeasons = new ArrayList<>();

		int lastSeason = endYear - seasons;
		
		for(int x = endYear; x> lastSeason; x--) {
			PlayerStatHolder holder = statsByYear.get(x);
			if(holder != null ) prevSeasons.add(holder);
		
		}

		return prevSeasons.isEmpty() ? new PlayerStatHolder() :  accumulateTotals(prevSeasons);
	}
	
	public PlayerStatHolder getPreviousSeasonTotals(int maxSeasons, int endYear) {

		List<PlayerStatHolder> prevSeasons = new ArrayList<>();

		int count = 0;
		for(PlayerStatHolder holder : getAllSeasons()) {
			if(holder.getYear() <= endYear) {
				count++;
				prevSeasons.add(holder);
			}
			
			if(count >= maxSeasons) break;
		
		}
		
		return prevSeasons.isEmpty() ? new PlayerStatHolder() :  accumulateTotals(prevSeasons);
	}
	

	
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
		return statsByYear.size();
	}

	public Integer getLastYearPlayed() {
		//return lastYearPlayed;
		return player.getSeasons().stream()
		.map(PlayerSeason::getYear)
		.max(Integer::compare).orElse(0);
	}
	

	public Integer getStartYear() {
		return startYear;
	}

	public Integer getEndYear() {
		return endYear;
	}
	
	//handle cases of incomplete NHL seasons (2019-2020)
	public void preProcess(PlayerSeason season) {
		
		Double multiplier = null;
		
		if(season.getYear() == 2019) {
			multiplier = 1.17; //assumes 70 game season
		}else if(season.getYear() == 2020) {
			multiplier = 1.4642; //assumes 70 game season		}
		}
		
		if(multiplier != null) {
			if(season.getStatsAll() != null) {

				season.getStatsAll().setGp((int) (season.getStatsAll().getGp() * multiplier));
				season.getStatsAll().setToi((int) (season.getStatsAll().getToi() * multiplier));
				
				season.getStatsAll().setGoals((int) (season.getStatsAll().getGoals() * multiplier));
				season.getStatsAll().setAssists((int) (season.getStatsAll().getAssists() * multiplier));
				
				season.getStatsAll().setPim((int) (season.getStatsAll().getPim() * multiplier));
				season.getStatsAll().setPenMinor((int) (season.getStatsAll().getPenMinor() * multiplier));
				season.getStatsAll().setPenMajor((int) (season.getStatsAll().getPenMajor() * multiplier));
				season.getStatsAll().setPenMisconduct((int) (season.getStatsAll().getPenMisconduct() * multiplier));
				
				season.getStatsAll().setGiveAway((int) (season.getStatsAll().getGiveAway() * multiplier));
				season.getStatsAll().setTakeAway((int) (season.getStatsAll().getTakeAway() * multiplier));
				season.getStatsAll().setShotsBlocked((int) (season.getStatsAll().getShotsBlocked() * multiplier));
				season.getStatsAll().setRushAttempt((int) (season.getStatsAll().getRushAttempt() * multiplier));
				
				season.getStatsAll().setHits((int) (season.getStatsAll().getHits()* multiplier));
			}

			if(season.getStatsOnIce() != null) {
				season.getStatsOnIce().setCf((int) (season.getStatsOnIce().getCf() * multiplier));
				season.getStatsOnIce().setCa((int) (season.getStatsOnIce().getCa() * multiplier));
				season.getStatsOnIce().setCfPct(season.getStatsOnIce().getCfPct());
				
				season.getStatsOnIce().setFf((int) (season.getStatsOnIce().getFf() * multiplier));
				season.getStatsOnIce().setFa((int) (season.getStatsOnIce().getFa() * multiplier));
				season.getStatsOnIce().setFfPct(season.getStatsOnIce().getFfPct());
				
				season.getStatsOnIce().setoZoneStart((int) (season.getStatsOnIce().getoZoneStart() * multiplier));
				season.getStatsOnIce().setdZoneStart((int) (season.getStatsOnIce().getdZoneStart() * multiplier));
				season.getStatsOnIce().setnZoneStart((int) (season.getStatsOnIce().getnZoneStart() * multiplier));
			}
			
			if(season.getStatsPp() != null) {
				season.getStatsPp().setToi((int) (season.getStatsPp().getToi() * multiplier));
				season.getStatsPp().setGoals((int) (season.getStatsPp().getGoals() * multiplier));
				season.getStatsPp().setAssists((int) (season.getStatsPp().getAssists() * multiplier));
				season.getStatsPp().setPoints((int) (season.getStatsPp().getPoints() * multiplier));
			}
			
			if(season.getStatsPk() != null) {
				season.getStatsPk().setToi((int) (season.getStatsPk().getToi() * multiplier));
			}
		}
	}
	
}
