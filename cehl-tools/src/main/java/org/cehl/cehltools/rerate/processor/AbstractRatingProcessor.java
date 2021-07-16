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

		Map<Double, Integer> map = new HashMap<>();
		if(last.getGp() > 0) {
			//RerateUtils.addToAverageMap(map,lastRating,last.getGp() + Math.max(50, last.getGp()));
			int weighting = Math.max(50, last.getGp());
			RerateUtils.addToAverageMap(map,lastRating,weighting);
		}else {
			//if player was injured last season and they played at least 20 games the season previous. Use that seasons rating again.
			if(lastPlayed.getGp() > 0 && last2.getGp() > 20) {
				//use previous full season.
				int lastPlayedRating = getSeasonRating(player,lastPlayed);
				//RerateUtils.addToAverageMap(map,lastPlayedRating * 0.95,100);
				int weighting = Math.max(50, last.getGp());
				RerateUtils.addToAverageMap(map,lastPlayedRating,weighting);
			}
		}
		if(last2.getGp() > 0) {
			//RerateUtils.addToAverageMap(map,last2Rating,last2.getGp() + Math.max(25, last2.getGp()));
			
			int weighting = Math.min(40, last2.getGp());
			RerateUtils.addToAverageMap(map,last2Rating,weighting);

		}
		if(last3.getGp() > 0) {
			//RerateUtils.addToAverageMap(map,last3Rating,last3.getGp());\
			int weighting = Math.min(20, last3.getGp());
			RerateUtils.addToAverageMap(map,last3Rating,weighting);
		}
		
		//if no gp used last seasons games played
//		if(totalGp == 0) {
//			if(lastPlayed.getGp() > 0) {
//				totalGp = (int) ((double)lastPlayed.getGp());
//			}
//		}

		if(map.isEmpty() && accumulator.getTotalStats().getGp() > 0) {

			int lastPlayedRating = getSeasonRating(player,lastPlayed);
			
			RerateUtils.addToAverageMap(map,lastPlayedRating,100);
			RerateUtils.addToAverageMap(map,60,100);
		}	
//		else if(last.getGp() == 0 && last2.getGp() > 0) {
//			RerateUtils.addToAverageMap(map,lastRating,last.getGp() + Math.max(50, last.getGp()));
//		}
		
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
		//rating = adjustRating(rating, totalGp);
		
		//adjust down players that don't have enough gp.
		//or players that no longer play in the nhl.
		int last3Gp = last.getGp() + last2.getGp() + last3.getGp();
		int careerGp = accumulator.getTotalStats().getGp();
		
		//this is to handle shortened seasons.
//		if(careerGp < 164 && last3Gp < 164) {
//			rating = adjustRating(rating, Math.max(careerGp, last3Gp));
//		}else if (last.getGp() == 0 && last2.getGp() < 20) {
//			rating = adjustRating(rating, last3Gp);
//		}
		
		return RerateUtils.normalizeRating(rating);
	}
	
	//adjust by limiting rating based on total nhl games played
	protected double adjustRating(double rating, int totalGp) {
		if (totalGp <= 20) {
			rating =  Math.min(rating, 69);
		} else if (totalGp <= 40) {
			rating = Math.min(rating, 71);
		} else if (totalGp <= 55) {
			rating = Math.min(rating, 73);
		} else if (totalGp <= 65) {
			rating = Math.min(rating, 75);
		} else if (totalGp <= 75) {
			rating = Math.min(rating, 77);
		}else if (totalGp <= 82) {
			rating = Math.min(rating, 79);
		}else if (totalGp <= 120) {
			rating = Math.min(rating, 80);
		}else if (totalGp <= 164) {
			rating = Math.min(rating, 82);
		}

		return rating;
	}
	
//	//adjust by limiting rating based on total nhl games played
//	protected double adjustRating(double rating, int totalGp) {
//		if (totalGp <= 20) {
//			rating =  Math.min(rating, 69);
//		} else if (totalGp <= 40) {
//			rating = Math.min(rating, 70);
//		} else if (totalGp <= 55) {
//			rating = Math.min(rating, 74);
//		} else if (totalGp <= 65) {
//			rating = Math.min(rating, 76);
//		} else if (totalGp <= 75) {
//			rating = Math.min(rating, 78);
//		}else if (totalGp <= 82) {
//			rating = Math.min(rating, 79);
//		}else if (totalGp <= 120) {
//			rating = Math.min(rating, 80);
//		}else if (totalGp <= 164) {
//			rating = Math.min(rating, 81);
//		}else if (totalGp <= 200) {
//			rating = Math.min(rating, 83);
//		}
////		else if (totalGp <= 246) {
////			rating = Math.min(rating, 87);
////		}
//
//		return rating;
//	}

}
