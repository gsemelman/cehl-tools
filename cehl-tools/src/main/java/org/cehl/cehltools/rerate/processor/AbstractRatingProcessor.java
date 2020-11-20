package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public abstract class AbstractRatingProcessor implements RatingProcessor2{
	
	
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		
		PlayerStatHolder last = accumulator.getPreviousSeasonTotalsFromYear(1, accumulator.getEndYear());	
		PlayerStatHolder last2 = accumulator.getPreviousSeasonTotalsFromYear(1, accumulator.getEndYear()-1);	
		PlayerStatHolder last3 = accumulator.getPreviousSeasonTotalsFromYear(1, accumulator.getEndYear()-2);
		PlayerStatHolder lastPlayed = accumulator.getPreviousSeasonsTotals(1);
		
		
		int lastRating = getSeasonRating(player,last);
		int last2Rating = getSeasonRating(player,last2);
		int last3Rating = getSeasonRating(player,last3);
		
		//double rating = ((lastRating * 1.2) + (last3Rating * 0.8)) / 2;
		double rating = 0;
		
		int totalGp = last.getGp() + last2.getGp() + last3.getGp();
		
		Map<Double, Integer> map = new HashMap<>();
		if(last.getGp() > 0) {
			RerateUtils.addToAverageMap(map,lastRating,last.getGp() + Math.max(50, last.getGp()));
		}else {
			if(lastPlayed.getGp() > 0) {
				int lastPlayedRating = getSeasonRating(player,lastPlayed);
				RerateUtils.addToAverageMap(map,lastPlayedRating * 0.9,100);
			}
		}
		if(last2.getGp() > 0) {
			RerateUtils.addToAverageMap(map,last2Rating,last2.getGp() + Math.max(25, last2.getGp()));
		}
		if(last3.getGp() > 0) {
			RerateUtils.addToAverageMap(map,last3Rating,last3.getGp());
		}
		
		//if no gp used last seasons games played
		if(totalGp == 0) {
			if(lastPlayed.getGp() > 0) {
				totalGp = (int) ((double)lastPlayed.getGp());
			}
		}

		if(map.isEmpty() && accumulator.getTotalStats().getGp() > 0) {

			int lastPlayedRating = getSeasonRating(player,lastPlayed);
			
			RerateUtils.addToAverageMap(map,lastPlayedRating,100);
			RerateUtils.addToAverageMap(map,60,100);
		}	
		else if(last.getGp() == 0 && last2.getGp() > 0) {
			RerateUtils.addToAverageMap(map,lastRating,last.getGp() + Math.max(50, last.getGp()));
		}
		
		rating = RerateUtils.calculateWeightedAverage(map);

		//rating = adjustRating(rating, accumulator.getTotalStats().getGp());
		//get total gp of last 3 seasons

		
//		//if no gp used last seasons games played
//		if(totalGp == 0) {
//			PlayerStatHolder lastPlayed = accumulator.getPreviousSeasonsTotals(1);
//			if(lastPlayed.getGp() > 0) {
//				totalGp = (int) ((double)lastPlayed.getGp() / 1.5);
//			}
//		}
		rating = adjustRating(rating, totalGp);
		
		return RerateUtils.normalizeRating(rating);
	}
	
	public double getRating4(Player player, PlayerStatAccumulator accumulator) {
		
		PlayerStatHolder totalLast = accumulator.getPreviousSeasonsTotals(1);	
		//PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonTotalsFromYear(3, accumulator.getEndYear());	
		//PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonTotalsFromYear(3, accumulator.getEndYear());	
		PlayerStatHolder totalLast2 = accumulator.getPreviousSeasonTotalsFromYear(2, accumulator.getEndYear()-1);	
		PlayerStatHolder totalsLast6 = accumulator.getPreviousSeasonTotalsFromYear(6, accumulator.getEndYear());
		
		int lastRating = getSeasonRating(player,totalLast);
		int last2Rating = getSeasonRating(player,totalLast2);
		//int last6Rating = getSeasonRating(player,totalsLast6);
		
		//double rating = ((lastRating * 1.2) + (last3Rating * 0.8)) / 2;
		double rating = 0;
		
		if((totalLast2.getGp() == 0 || totalLast2.getGp() < 20) && totalsLast6.getGp() <= 82) {
			rating = lastRating;
			//rating = adjustSeasonRating2(lastRating, totalsLast6.getGp());
			
			rating = adjustRating2(lastRating, totalsLast6.getGp());
			
		}else {
			rating = ((lastRating * 1.2) + (last2Rating * 0.8)) / 2;
			rating = adjustRating(rating, totalsLast6.getGp());
		}
		
		
		
		return RerateUtils.normalizeRating(rating);
	}
	
	public double getRating3(Player player, PlayerStatAccumulator accumulator) {
	
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
			rating =  Math.min(rating, 7);
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
	
	protected double adjustRating2(double rating, int totalGp) {
		if (totalGp <= 10) {
			rating =  Math.min(rating, 72);
		} else if (totalGp <= 20) {
			rating = Math.min(rating, 74);
		} else if (totalGp <= 30) {
			rating = Math.min(rating, 77);
		} else if (totalGp <= 40) {
			rating = Math.min(rating, 78);
		} else if (totalGp <= 50) {
			rating = Math.min(rating, 80);
		}else if (totalGp <= 60) {
			rating = Math.min(rating, 82);
		}else if (totalGp <= 70) {
			rating = Math.min(rating, 83);
		}
//		else if (totalGp <= 246) {
//			rating = Math.min(rating, 87);
//		}

		return rating;
	}
	
	
	
	
	protected double adjustSeasonRating2(double rating, int totalGp) {
//		if (totalGp <= 20) {
//			rating =  rating * 0.83;
//		}else if (totalGp <= 40) {
//			rating = rating * 0.85;
//		}else if (totalGp <= 50) {
//			rating = rating * 0.87;
//		}else if (totalGp <= 60) {
//			rating = rating * 0.88;
//		}else {
//			rating = rating * 0.89;
//		}
//		else if (totalGp <= 65) {
//			rating = rating * 0.98;
//		}
		
		if(rating >= 84) {
			if (totalGp <= 20) {
				rating =  rating * 0.85;
			}else if (totalGp <= 40) {
				rating = rating * 0.92;
			}else if (totalGp <= 50) {
				rating = rating * 0.93;
			}else if (totalGp <= 60) {
				rating = rating * 0.94;
			}else {
				rating = rating * 0.95;
			}
		}else if(rating >= 78) {
			if (totalGp <= 20) {
				rating =  rating * 0.85;
			}else if (totalGp <= 40) {
				rating = rating * 0.93;
			}else if (totalGp <= 50) {
				rating = rating * 0.95;
			}else if (totalGp <= 60) {
				rating = rating * 0.96;
			}else {
				rating = rating * 0.97;
			}

		}else {
			if (totalGp <= 20) {
				rating =  rating * 0.85;
			}else if (totalGp <= 40) {
				rating = rating * 0.94;
			}else if (totalGp <= 50) {
				rating = rating * 0.95;
			}else if (totalGp <= 60) {
				rating = rating * 0.96;
			}else {
				rating = rating * 0.97;
			}
		}
		
		return rating;
	}
	

}
