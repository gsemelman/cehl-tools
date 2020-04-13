package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public abstract class AbstractRatingProcessor implements RatingProcessor2{
	
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
	
		//last season played
		PlayerStatHolder totalLast = accumulator.getPreviousSeasonsTotals(1);	
		//last 3 season played
		PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonsTotals(3);		
		//last 6 season played
		//PlayerStatHolder totalsLast6 = accumulator.getPreviousSeasonTotals(6, accumulator.getLastYearPlayed());
		PlayerStatHolder totalsLast6 = accumulator.getPreviousSeasonTotalsFromYear(6, accumulator.getEndYear());
		//last 3 seasons from current rerate year even if the player did not play
		PlayerStatHolder last3IfPlayed = accumulator.getPreviousSeasonTotalsFromYear(2, accumulator.getEndYear()-1);
		
		int lastRating = getSeasonRating(player,totalLast);
		int last3Rating = getSeasonRating(player,totalLast3);
		int last6Rating = getSeasonRating(player,totalsLast6);
		int last3IfPlayedRating = getSeasonRating(player,last3IfPlayed);

		Map<Double, Integer> map = new HashMap<>();
		
//		//add last season weight on games played
//		//RerateUtils.addToAverageMap(map,lastRating,totalLast.getGp());
//		RerateUtils.addToAverageMap(map,adjustSeasonRating(lastRating,totalLast.getGp()),totalLast.getGp());
//		
//		//add last 3 seasons weight on games played
//		//RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
//		//RerateUtils.addToAverageMap(map,last3IfPlayedRating,Math.max(last3IfPlayed.getGp(), 100));
//		//RerateUtils.addToAverageMap(map,adjustRating(last3IfPlayedRating, last3IfPlayed.getGp()),totalLast3.getGp());
//		RerateUtils.addToAverageMap(map,last3IfPlayedRating,Math.max(last3IfPlayed.getGp(), 100));

//		//for older players where the last 6 seasons rating is better than the last 3
//		//use the totals of the last 6 and divid by seasons played.
//		if(last6Rating > last3Rating) {
//			RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / totalsLast6.getYear());
//		}
		
//		//calculate rating
//		double rating = RerateUtils.calculateWeightedAverage(map);
		
		Double rating = (double) 0;
		
		//use total average to determine rating for players played less than 3 seasons.
		if(accumulator.getTotalSeasons() <= 2 || accumulator.getTotalStats().getGp() <= 82) {
			RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
			
			rating= RerateUtils.calculateWeightedAverage(map);
		}else {
			//for all other players.

			int lastSeasonWeight = (int) (totalLast.getGp() * 1.25);
			PlayerStatHolder last4to6 = accumulator.getPreviousSeasonTotalsFromYear(3, accumulator.getEndYear()-3);
			double lat4to6Rating = getSeasonRating(player,last4to6);
			
			RerateUtils.addToAverageMap(map,adjustSeasonRating(lastRating,totalLast.getGp()),lastSeasonWeight);
			RerateUtils.addToAverageMap(map,last3IfPlayedRating,last3IfPlayed.getGp());
			
			rating= RerateUtils.calculateWeightedAverage(map);
			
			//for older players where the last 6 seasons rating is better than the last 3
			//use the totals of the last 6 and divid by seasons played.
			if(last4to6.getYear() > 0 && accumulator.getTotalSeasons() > 3 
					&& accumulator.getTotalStats().getGp() > 140 && lat4to6Rating > rating) {
				//RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / totalsLast6.getYear());

				RerateUtils.addToAverageMap(map,lat4to6Rating,last4to6.getGp() / last4to6.getYear());
				rating= RerateUtils.calculateWeightedAverage(map);
			}
		}

		
		int endYearMinusOne = accumulator.getEndYear() -1 ;
		int endYearMinusTwo = accumulator.getEndYear() -1 ;;

		if(accumulator.getLastYearPlayed() <= endYearMinusTwo && accumulator.getTotalSeasons() > 3) {
			rating = adjustRating(rating, last3IfPlayed.getGp());
		}else if(accumulator.getLastYearPlayed() <= endYearMinusOne && accumulator.getTotalSeasons() > 3 && player.getAge() >= 33) {
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
	
		if(rating.isInfinite() || rating.isNaN()) {
			return 60;
		}
		
		return RerateUtils.normalizeRating(rating);

	}
	
	public double getRating2(Player player, PlayerStatAccumulator accumulator) {
		
		//last season played
		PlayerStatHolder totalLast = accumulator.getPreviousSeasonsTotals(1);	
		//last 3 season played
		PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonsTotals(3);		
		//last 6 season played
		PlayerStatHolder totalsLast6 = accumulator.getPreviousSeasonTotals(6, accumulator.getLastYearPlayed());
		//last 3 seasons from current rerate year even if the player did not play
		PlayerStatHolder last3IfPlayed = accumulator.getPreviousSeasonTotalsFromYear(2, accumulator.getEndYear()-1);
		
		int lastRating = getSeasonRating(player,totalLast);
		int last3Rating = getSeasonRating(player,totalLast3);
		int last6Rating = getSeasonRating(player,totalsLast6);
		int last3IfPlayedRating = getSeasonRating(player,last3IfPlayed);

		Map<Double, Integer> map = new HashMap<>();
		
//		//add last season weight on games played
//		//RerateUtils.addToAverageMap(map,lastRating,totalLast.getGp());
//		RerateUtils.addToAverageMap(map,adjustSeasonRating(lastRating,totalLast.getGp()),totalLast.getGp());
//		
//		//add last 3 seasons weight on games played
//		//RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
//		//RerateUtils.addToAverageMap(map,last3IfPlayedRating,Math.max(last3IfPlayed.getGp(), 100));
//		//RerateUtils.addToAverageMap(map,adjustRating(last3IfPlayedRating, last3IfPlayed.getGp()),totalLast3.getGp());
//		RerateUtils.addToAverageMap(map,last3IfPlayedRating,Math.max(last3IfPlayed.getGp(), 100));

//		//for older players where the last 6 seasons rating is better than the last 3
//		//use the totals of the last 6 and divid by seasons played.
//		if(last6Rating > last3Rating) {
//			RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / totalsLast6.getYear());
//		}
		
		//use total average to determine rating for players played less than 3 seasons.
		if(accumulator.getTotalSeasons() <= 2 || accumulator.getTotalStats().getGp() <= 82) {
			RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
		}else {
			//for all other players.

			RerateUtils.addToAverageMap(map,adjustSeasonRating(lastRating,totalLast.getGp()),totalLast.getGp());
			RerateUtils.addToAverageMap(map,last3IfPlayedRating,last3IfPlayed.getGp());
			
			//for older players where the last 6 seasons rating is better than the last 3
			//use the totals of the last 6 and divid by seasons played.
			if(accumulator.getTotalSeasons() > 3 && accumulator.getTotalStats().getGp() > 140 && last6Rating > last3Rating) {
				RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / totalsLast6.getYear());
			}
		}
		
		//calculate rating
		double rating = RerateUtils.calculateWeightedAverage(map);
		
		int endYearMinusOne = accumulator.getEndYear() -1 ;
		int endYearMinusTwo = accumulator.getEndYear() -1 ;;

		if(accumulator.getLastYearPlayed() <= endYearMinusTwo && accumulator.getTotalSeasons() > 3) {
			rating = adjustRating(rating, last3IfPlayed.getGp());
		}else if(accumulator.getLastYearPlayed() <= endYearMinusOne && accumulator.getTotalSeasons() > 3 && player.getAge() >= 33) {
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
		}
//		else if (totalGp <= 246) {
//			rating = Math.min(rating, 87);
//		}

		return rating;
	}
	
	protected double adjustSeasonRating(double rating, int totalGp) {
		if (totalGp <= 20) {
			rating =  rating * 0.85;
		}else if (totalGp <= 40) {
			rating = rating * 0.925;
		}else if (totalGp <= 50) {
			rating = rating * 0.95;
		}else if (totalGp <= 60) {
			rating = rating * 0.97;
		}
//		else if (totalGp <= 65) {
//			rating = rating * 0.98;
//		}
		
		return rating;
	}
	

}
