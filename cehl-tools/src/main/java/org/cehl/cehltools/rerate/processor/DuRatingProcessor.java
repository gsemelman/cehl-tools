package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class DuRatingProcessor implements RatingProcessor2{
	
	static RangeTable rangeTable = initRange();

	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0.2, 60);
		rangeTable.insertValue(0.3, 63);
		rangeTable.insertValue(0.4, 64);
		rangeTable.insertValue(0.5, 65);
		rangeTable.insertValue(0.7, 66);
		rangeTable.insertValue(0.8, 68);
		rangeTable.insertValue(0.9, 70);
		rangeTable.insertValue(1, 82); //played all games
		
		return rangeTable;

	}
	
	@Override
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		PlayerStatHolder allSeasons = accumulator.getTotalStats();
		double allRating = getSeasonRating(player,allSeasons);
		
		PlayerStatHolder last3IfPlayed = accumulator.getPreviousSeasonTotalsFromYear(3, accumulator.getEndYear());
		double last3Rating = getSeasonRating(player,last3IfPlayed);
		
		Map<Double, Integer> map = new HashMap<>();
		RerateUtils.addToAverageMap(map,allRating, 50);
		RerateUtils.addToAverageMap(map,last3Rating, 150);
	

		double rating = RerateUtils.calculateWeightedAverage(map);
		
		if(player.getAge() >= 35) {
			rating = rating * 1.05;
		}else if(player.getAge() >= 32) {
			rating = rating * 1.03;
		}else if(player.getAge() >= 28) {
			rating = rating * 1.02;
		}else if(player.getAge() >= 25) {
			rating = rating * 1.01;
		}
		
		rating = Math.max(rating, 60);
		rating = Math.min(rating, 95);
		
		return RerateUtils.normalizeRating(adjustRating(rating, allSeasons.getGp()));
		
		//return adjustRating(getSeasonRating(player,allSeasons),allSeasons.getGp());
	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = statHolder.getGp();

		double playedPct = gp / (statHolder.getYear() * 82);//assumes year holds total seasons;
		
		double du = Double.valueOf(rangeTable.findInterpolatedValue(playedPct));
		
		du = Math.max(du, 60);
		du = Math.min(du, 95);
		
		return RerateUtils.normalizeRating(du);
	}
	
	protected double adjustRating(double rating, int gp) {
		if (gp <= 20) {
			rating =  Math.min(rating, 66);
		} else if (gp <= 40) {
			rating = Math.min(rating, 67);
		} else if (gp <= 55) {
			rating = Math.min(rating, 68);
		} else if (gp <= 65) {
			rating = Math.min(rating, 69);
		} else if (gp <= 75) {
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
