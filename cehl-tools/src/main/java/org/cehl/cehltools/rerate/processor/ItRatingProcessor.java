package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ItRatingProcessor extends AbstractRatingProcessor{


	static RangeTable hitsTable = hitsRange();
	static RangeTable takeAwayTable = takeAwayRange();
	static RangeTable shotsBlockedTable = shotsBlockedRange();
	

	static RangeTable hitsRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(1.1, 70);
		rangeTable.insertValue(4, 94); 
		
		return rangeTable;

	}
	
	static RangeTable takeAwayRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(0.46, 70);
		rangeTable.insertValue(1.4, 95); 
		
		return rangeTable;

	}
	
	static RangeTable shotsBlockedRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(1.3, 70);
		rangeTable.insertValue(2.9, 95); 
		
		return rangeTable;
	}
	
	
	static RangeTable rangeTable = initRange();
	//total range
	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
//		rangeTable.insertValue(0, 60);
//		rangeTable.insertValue(5, 70);
//		rangeTable.insertValue(7, 73);
//		rangeTable.insertValue(12, 77);
//		rangeTable.insertValue(22, 90); 
		
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(5, 67);
		rangeTable.insertValue(7, 71);
		rangeTable.insertValue(12, 77);
		rangeTable.insertValue(22, 90); 
		
		return rangeTable;

	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		double hits = statHolder.getHits();
		double takeAway = statHolder.getTakeAway();
		double shotsBlocked = statHolder.getShotsBlocked();
		double pim = statHolder.getPim();

		double hitsPerGame = hits / statHolder.getGp();
		double takeAwayGame = takeAway / statHolder.getGp();;
		double shotsBlockedGame = shotsBlocked / statHolder.getGp();;
		double pimGame = pim / statHolder.getGp();
		
		if(player.getPosition().contains("D")) {
			//takeAwayGame = takeAwayGame * 1.222;
			takeAwayGame = takeAwayGame * 1.222;
		}else {
			shotsBlockedGame = shotsBlockedGame * 2.5;
		}

		
		double hitsRating = Double.valueOf(hitsTable.findInterpolatedValue(hitsPerGame));
		double takeAwayRating = Double.valueOf(takeAwayTable.findInterpolatedValue(takeAwayGame));
		double shotsBlockedRating = Double.valueOf(shotsBlockedTable.findInterpolatedValue(shotsBlockedGame));

		Map<Double, Integer> map = new HashMap<>();
		RerateUtils.addToAverageMap(map,hitsRating,100);
		RerateUtils.addToAverageMap(map,takeAwayRating,40);
		RerateUtils.addToAverageMap(map,shotsBlockedRating,30);

		Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
		
		double pimBonus = 0;
		
		if(pimGame > 2) {
			pimBonus = 10;
		}else if(pimGame > 1.3) {
			pimBonus = 7;
		}else if(pimGame > 0.9) {
			pimBonus = 5;
		}else if(pimGame > 0.5) {
			pimBonus = 2;
		}else if(pimGame > 0.4) {
			pimBonus = 1;
		}
		
		double it = pimBonus + weightedAverage;
		
		it = Math.max(it, 60);
		
		return RerateUtils.normalizeRating(it) ;
	}
	

	
	//@Override
	public int getSeasonRating2(Player player, PlayerStatHolder statHolder) {
		double toi = statHolder.getToi();
		double toiPer60 = (toi/60);
		double hits = statHolder.getHits();
		double shotsBlocked = statHolder.getShotsBlocked();
		double rushAttempt = statHolder.getRushAttempt();
		double pim = statHolder.getPim();
		double takeAway = statHolder.getTakeAway();
		

		double hitsPerMin = hits / toiPer60;
		double shotsBlockedPerMin = shotsBlocked / toiPer60;
		double rushAttemptPerMin = rushAttempt / toiPer60;
		
		Map<Double, Integer> map = new HashMap<>();
		
		if(player.getPosition().contains("D")) {
			hitsPerMin = hitsPerMin * 1.25;
			shotsBlockedPerMin = shotsBlockedPerMin * 2;
		}else{
			rushAttemptPerMin = rushAttemptPerMin * 1.5;
		}
		
		
		
		RerateUtils.addToAverageMap(map,hitsPerMin,100);
		RerateUtils.addToAverageMap(map,shotsBlockedPerMin,50);
		RerateUtils.addToAverageMap(map,Math.max(rushAttemptPerMin, pim/10),150);

		Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
		
		double it = Double.valueOf(rangeTable.findInterpolatedValue(weightedAverage));

		return RerateUtils.normalizeRating(it) ;
	}

}
