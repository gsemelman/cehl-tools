package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ExRatingProcessor extends AbstractRatingProcessor{
	
	static RangeTable rangeTable = initRange();

	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(0.7, 67);
		rangeTable.insertValue(0.8, 70);
		rangeTable.insertValue(0.9, 74);
		rangeTable.insertValue(0.98, 80);
		rangeTable.insertValue(1, 95); //played all games
		
		return rangeTable;

	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = statHolder.getGp();
		double age = RerateUtils.calculateAge(player.getDob());
		
		double gpStat = (gp * 0.055);
		double seasonStat = statHolder.getYear() * 0.7; //assumes seasons is set into years var
		double ageStat = age / 3;

		double ex = gpStat + seasonStat + ageStat + 30;
		
		if(age > 35) {
			ex = ex+5;
		}

		return RerateUtils.normalizeRating(ex);
	}

}
