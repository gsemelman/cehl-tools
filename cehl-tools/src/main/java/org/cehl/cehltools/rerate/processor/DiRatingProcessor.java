package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class DiRatingProcessor implements RatingProcessor2{
	
	static RangeTable pimRangeTable() {
		RangeTable majorTable = new RangeTable();
		majorTable.insertValue(0, 84);
		majorTable.insertValue(0.022, 70);
		majorTable.insertValue(0.15, 45);
		
		return majorTable;

	}
	static RangeTable majorRangerTable() {
		RangeTable majorTable = new RangeTable();
		majorTable.insertValue(0, 0);
		majorTable.insertValue(5, 5);
		majorTable.insertValue(15, 10); //played all games
		
		return majorTable;

	}
	
	
	
	@Override
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		PlayerStatHolder allSeasons = accumulator.getTotalStats();
		return adjustRating(getSeasonRating(player,allSeasons),allSeasons.getGp());
	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
//		double toi = statHolder.getToi();
//		double pim = statHolder.getPim();
//		
//		double pimPerMin = pim/toi;
//
//		double di;
//		
//		di = (89 - (pimPerMin / 0.00141));
//		
//		//min of 45 di
//		di = Math.max(di, 45);
		
		double toi = statHolder.getToi();
		double pim = statHolder.getPim();
		
		double pimPerMin = pim/toi;
		double di = Double.valueOf(pimRangeTable().findInterpolatedValue(pimPerMin));


		return RerateUtils.normalizeRating(di);
	}

	protected double adjustRating(double rating, int gp) {
		if (gp <= 75) {
			rating = Math.min(rating, 70);
		}else if (gp <= 82) {
			rating = Math.min(rating, 71);
		}else if (gp <= 164) {
			rating = Math.min(rating, 75);
		}else if (gp <= 200) {
			rating = Math.min(rating, 76);
		}else if (gp <= 246) {
			rating = Math.min(rating, 78);
		}
		
		return rating;
	}

}
