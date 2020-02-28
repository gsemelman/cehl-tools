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
		rangeTable.insertValue(2, 70);
		rangeTable.insertValue(4, 73);
		rangeTable.insertValue(7, 77);
		rangeTable.insertValue(15, 90); 
		
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
		map.put((double) hitsPerMin, 100);
		//map.put((double) shotsBlockedPerMin, 30);
		map.put((double) Math.max(rushAttemptPerMin, pim/10), 100);
		Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
		
		double it = Double.valueOf(rangeTable.findInterpolatedValue(weightedAverage));

		return RerateUtils.normalizeRating(it);
	}

}
