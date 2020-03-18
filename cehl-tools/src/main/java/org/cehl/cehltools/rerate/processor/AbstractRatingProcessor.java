package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public abstract class AbstractRatingProcessor implements RatingProcessor2{

	private boolean useTotal = false;
	
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
	
		//last season played
		PlayerStatHolder totalLast = accumulator.getPreviousSeasonsTotals(1);	
		//last 3 season played
		PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonsTotals(3);		
		//last 6 season played
		PlayerStatHolder totalsLast6 = accumulator.getPreviousSeasonTotals(6, accumulator.getLastYearPlayed());
		
		//last 3 seasons from current rerate year even if the player did not play
		PlayerStatHolder last3IfPlayed = accumulator.getPreviousSeasonTotalsFromYear(3, 2018);
		int last3gp = totalLast3.getGp();
		
		int lastRating = getSeasonRating(player,totalLast);
		int last3Rating = getSeasonRating(player,totalLast3);
		int last6Rating = getSeasonRating(player,totalsLast6);

		
		if(useTotal) return last3Rating;

		Map<Double, Integer> map = new HashMap<>();
		
		//add last season weight on games played
		RerateUtils.addToAverageMap(map,lastRating,totalLast.getGp());
		
		//add last 3 seasons weight on games played
		RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());

		//for older players where the last 6 seasons rating is better than the last 3
		//use the totals of the last 6 and divid by seasons played.
		if(last6Rating > last3Rating) {
			RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / totalsLast6.getYear());
		}
		
		//calculate rating
		double rating = RerateUtils.calculateWeightedAverage(map);

		if(accumulator.getLastYearPlayed() <= 2016 && accumulator.getTotalSeasons() > 3) {
			rating = adjustRating(rating, last3IfPlayed.getGp());
		}else if(accumulator.getLastYearPlayed() <= 2017 && accumulator.getTotalSeasons() > 3) {
			rating = adjustRating(rating, last3IfPlayed.getGp());
		}else {
			//handle players with less than 3 seasons
			rating = adjustRating(rating, accumulator.getTotalStats().getGp());
		}
		
//		//rating = adjustRating(rating, accumulator.getTotalStats().getGp());
//		if(last3IfPlayed.getGp() <= 82 && accumulator.getTotalSeasons() > 3) {
//			rating = adjustRating(rating, last3IfPlayed.getGp());
//		}else {
//			rating = adjustRating(rating, accumulator.getTotalStats().getGp());
//		}
	
		
		
		return RerateUtils.normalizeRating(rating);

	}
	
	//adjust by limiting rating based on total nhl games played
	protected double adjustRating(double rating, int totalGp) {
		if (totalGp <= 20) {
			rating =  Math.min(rating, 69);
		} else if (totalGp <= 40) {
			rating = Math.min(rating, 70);
		} else if (totalGp <= 55) {
			rating = Math.min(rating, 74);
		} else if (totalGp <= 65) {
			rating = Math.min(rating, 76);
		} else if (totalGp <= 75) {
			rating = Math.min(rating, 78);
		}else if (totalGp <= 82) {
			rating = Math.min(rating, 79);
		}else if (totalGp <= 120) {
			rating = Math.min(rating, 80);
		}else if (totalGp <= 164) {
			rating = Math.min(rating, 81);
		}else if (totalGp <= 200) {
			rating = Math.min(rating, 83);
		}else if (totalGp <= 246) {
			rating = Math.min(rating, 84);
		}

		return rating;
	}
	

}
