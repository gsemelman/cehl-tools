package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ItRatingProcessor extends AbstractRatingProcessor{
	static RangeTable rangeTable = initRange();

	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(5, 70);
		rangeTable.insertValue(7, 72);
		rangeTable.insertValue(12, 75);
		rangeTable.insertValue(22, 90); 
		
		return rangeTable;

	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		double toi = statHolder.getToi();
		double toiPer60 = (toi/60);
		double hits = statHolder.getHits();
		double shotsBlocked = statHolder.getShotsBlocked();
		double rushAttempt = statHolder.getRushAttempt();
		double pim = statHolder.getPim();
		
		  

		double hitsPerMin = hits / toiPer60;
		double shotsBlockedPerMin = shotsBlocked / toiPer60;
		double rushAttemptPerMin = rushAttempt / toiPer60;
		
		Map<Double, Integer> map = new HashMap<>();
		
		if(player.getPosition().contains("D")) {
			hitsPerMin = hitsPerMin * 1.25;
		}
		
		RerateUtils.addToAverageMap(map,hitsPerMin,100);
		double sbWeight = player.getPosition().contains("D") ? 2 : 1;
		RerateUtils.addToAverageMap(map,shotsBlockedPerMin * sbWeight,30);
		RerateUtils.addToAverageMap(map,Math.max(rushAttemptPerMin, pim/10),100);

		Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
		
		double it = Double.valueOf(rangeTable.findInterpolatedValue(weightedAverage));

		return RerateUtils.normalizeRating(it) ;
	}

}
