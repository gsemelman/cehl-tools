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
		
		RerateUtils.addToAverageMap(map,lastRating,totalLast.getGp());
		RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
		
//		RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
//		
//		if(last3Rating > lastRating) {
//			RerateUtils.addToAverageMap(map,last3Rating,totalLast3.getGp());
//		}
		
		if(last6Rating > last3Rating) {
			RerateUtils.addToAverageMap(map,last6Rating,totalsLast6.getGp() / 2);
		}
		
		double rating = RerateUtils.calculateWeightedAverage(map);
		
		boolean totalLast3Seasons = accumulator.getTotalSeasons() > 3;
		
		//rating = adjustRating(rating, accumulator.getTotalStats().getGp());
		if(last3IfPlayed.getGp() <= 82 && accumulator.getTotalSeasons() > 3) {
			rating = adjustRating(rating, last3IfPlayed.getGp());
		}else {
			rating = adjustRating(rating, accumulator.getTotalStats().getGp());
		}
	
		
		
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
	


	public double getRating2(Player player, PlayerStatAccumulator accumulator) {
		PlayerStatHolder last = null;
		PlayerStatHolder last2 = null;
		PlayerStatHolder last3 = null;
		PlayerStatHolder totals = accumulator.getTotalStats();
		PlayerStatHolder totalLast3 = accumulator.getPreviousSeasonsTotals(3);
		
		PlayerStatHolder totals2 = accumulator.getPreviousSeasonTotals(5, accumulator.getLastYearPlayed() - 3);
		
		int count = 1;
		for(PlayerStatHolder stat: accumulator.getPreviousSeasonsStats(3)) {
			
			if(count == 1) {
				last = stat;
			}else if(count == 2){
				last2 = stat;
			}else if(count == 3){
				last3 = stat;
				break;
			}else {
				break;
			}
			
			count ++;
		}
		
		int lastRating = last != null ? getSeasonRating(player,last) : 0;
		int last2Rating= last2 != null ? getSeasonRating(player,last2) : 0;
		int last3Rating= last3 != null ? getSeasonRating(player,last3) : 0;
		
		lastRating = Math.max(60, lastRating);
		last2Rating = Math.max(60, last2Rating);
		last3Rating = Math.max(60, last3Rating);
		int totalRating = getSeasonRating(player,totals);
		int last3TotalRating = getSeasonRating(player,totalLast3);
		
		if(useTotal) return last3TotalRating;

		Map<Double, Integer> map = new HashMap<>();
		RerateUtils.addToAverageMap(map, lastRating, last != null ? last.getGp(): 82);
		RerateUtils.addToAverageMap(map, last2Rating, last2 != null ? last2.getGp(): 82);
		 
		 if(accumulator.getTotalSeasons() >= 3) {
			RerateUtils.addToAverageMap(map, last3Rating, last3 != null ? last3.getGp(): 82);
		 }else if(accumulator.getTotalSeasons() == 2 && totalLast3.getGp() > 82) {
			 RerateUtils.addToAverageMap(map, last3Rating, last3 != null ? last3.getGp(): 20);
		 }else if(accumulator.getTotalSeasons() == 2 ) {
			 RerateUtils.addToAverageMap(map, last3Rating, last3 != null ? last3.getGp(): 40);
		 }
		 
		 if(totalLast3.getGp() > 82) {
			 //map.put((double) last3TotalRating, totalLast3.getGp()/3);
			 RerateUtils.addToAverageMap(map, last3TotalRating, totalLast3.getGp()/3);
		 }

	     Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
	     Double weightedAverage2 = null;
	     
		 //if(accumulator.getTotalSeasons() >= 4 && totalGroup.getGp() > 120) {
	     if(totals2.getGp() > 162) {
	 		int totalRating2 = getSeasonRating(player,totals2);
			// map.put((double) totalRating, 20);
			// map.put((double) totalRating, 35);
			 //map.put((double) totalRating2, 20);
			 RerateUtils.addToAverageMap(map, totalRating2, 20);
			// weightedAverage2 = RerateUtils.calculateWeightedAverage(map);
		 }
	     
		 double finalRating = 0;
		
		if (totalLast3.getGp() < 20) {
			finalRating =  Math.min(last3TotalRating, 68);
		} else if (totalLast3.getGp() < 40) {
			finalRating = Math.min(last3TotalRating, 70);
		} else if (totalLast3.getGp() < 55) {
			finalRating = Math.min(last3TotalRating, 74);
		} else if (totalLast3.getGp() < 65) {
			finalRating = Math.min(last3TotalRating, 76);
		} else if (weightedAverage2 != null) {
			//return RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
			finalRating =  RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
		} else {
			finalRating = RerateUtils.normalizeRating(weightedAverage);
		}
		
		return finalRating;

	}

	public boolean isUseTotal() {
		return useTotal;
	}

	public void setUseTotal(boolean useTotal) {
		this.useTotal = useTotal;
	}
	

}
