package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class DuRatingProcessor extends AbstractRatingProcessor{
	
	static RangeTable rangeTable = initRange();

	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(0.7, 67);
		rangeTable.insertValue(0.8, 70);
		rangeTable.insertValue(0.9, 74);
		rangeTable.insertValue(0.98, 80);
		rangeTable.insertValue(1, 90); //played all games
		
		return rangeTable;

	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = statHolder.getGp();

		double playedPct = gp / (statHolder.getYear() * 82);//assumes year holds total seasons;
		
		double du = Double.valueOf(rangeTable.findInterpolatedValue(playedPct));
		
		du = Math.max(du, 60);

		return RerateUtils.normalizeRating(du);
	}

}
