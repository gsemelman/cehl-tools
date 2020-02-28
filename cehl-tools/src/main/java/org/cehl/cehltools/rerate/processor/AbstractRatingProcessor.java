package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public abstract class AbstractRatingProcessor implements RatingProcessor2{


	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		PlayerStatHolder last = null;
		PlayerStatHolder last2 = null;
		PlayerStatHolder last3 = null;
		PlayerStatHolder totals = accumulator.getTotalStats();
		PlayerStatHolder totalGroup = accumulator.getPreviousSeasonsTotals(3);
		
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
		
		Map<Double, Integer> map = new HashMap<>();
		 map.put((double) lastRating, last != null ? last.getGp() + 40 : 82);
		 map.put((double) last2Rating, last2 != null ? last2.getGp() + 20 : 82);
		 
		 if(accumulator.getTotalSeasons() >= 3) {
			 map.put((double) last3Rating, last3 != null ? last3.getGp() : 82); 
		 }else if(accumulator.getTotalSeasons() == 2 && totalGroup.getGp() > 82) {
			 map.put((double) last3Rating, last3 != null ? last3.getGp() : 20);
		 }else if(accumulator.getTotalSeasons() == 2 ) {
			 map.put((double) last3Rating, last3 != null ? last3.getGp() : 40);
		 }

	     Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
	     Double weightedAverage2 = null;
	     
		 if(accumulator.getTotalSeasons() >= 4 && totalGroup.getGp() > 120) {
			 map.put((double) totalRating, 35);
			 
			 weightedAverage2 = RerateUtils.calculateWeightedAverage(map);
		 }
	     
		 double finalRating = 0;
		
		if (totalGroup.getGp() < 20) {
			finalRating =  Math.min(totalRating, 68);
		} else if (totalGroup.getGp() < 40) {
			finalRating = Math.min(totalRating, 70);
		} else if (totalGroup.getGp() < 55) {
			finalRating = Math.min(totalRating, 74);
		} else if (totalGroup.getGp() < 70) {
			finalRating = Math.min(totalRating, 77);
		} else if (weightedAverage2 != null) {
			//return RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
			finalRating =  RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
		} else {
			finalRating = RerateUtils.normalizeRating(weightedAverage);
		}
		
		return finalRating;

	}
	
//	public double getRating(Player player, PlayerStatAccumulator accumulator) {
//		PlayerStatHolder last = null;
//		PlayerStatHolder last2 = null;
//		PlayerStatHolder last3 = null;
//		PlayerStatHolder totals = accumulator.getTotalStats();
//		PlayerStatHolder totalGroup = accumulator.getPreviousSeasonsTotals(3);
//		
//		int count = 1;
//		for(PlayerStatHolder stat: accumulator.getPreviousSeasonsStats(3)) {
//			
//			if(count == 1) {
//				last = stat;
//			}else if(count == 2){
//				last2 = stat;
//			}else if(count == 3){
//				last3 = stat;
//				break;
//			}else {
//				break;
//			}
//			
//			count ++;
//		}
//		
//		int lastRating = last != null ? getSeasonRating(player,last) : 0;
//		int last2Rating= last2 != null ? getSeasonRating(player,last2) : 0;
//		int last3Rating= last3 != null ? getSeasonRating(player,last3) : 0;
//		
//		lastRating = Math.max(60, lastRating);
//		last2Rating = Math.max(60, last2Rating);
//		last3Rating = Math.max(60, last3Rating);
//		
//		int totalRating = getSeasonRating(player,totals);
//		
//
//		
//		 Map<Double, Integer> map = new HashMap<>();
//		 
//		 if(accumulator.getTotalSeasons() >= 4 && totalGroup.getGp() > 100) {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) last3Rating, 50);
//			  map.put((double) totalRating, 50);
//		 }else if(accumulator.getTotalSeasons() >= 3 && totalGroup.getGp() > 160) {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) last3Rating, 50);
//		 }else if (accumulator.getTotalSeasons() >= 2 && totalGroup.getGp() > 100) {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) 60, 50);
//		 }else {
//			   //map.put((double) lastRating, 80);
//			 map.put((double) totalRating, 80);
//			   map.put((double) 60, 100);
//		 }
//		 
//
//	     Double weightedAverage = RerateUtils.calculateWeightedAverage(map);
//	     Double weightedAverage2 = RerateUtils.calculateWeightedAverage(map);
//		
//		if (totalGroup.getGp() < 20) {
//			return Math.min(totalRating, 68);
//		} else if (totalGroup.getGp() < 40) {
//			return Math.min(totalRating, 70);
//		} else if (totalGroup.getGp() < 55) {
//			return Math.min(totalRating, 74);
//		} else if (totalGroup.getGp() < 70) {
//			return Math.min(totalRating, 77);
//		} else if (accumulator.getAllSeasons().size() > 30) {
//			return RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
//		} else {
//			return RerateUtils.normalizeRating(weightedAverage);
//		}
//	}

}
