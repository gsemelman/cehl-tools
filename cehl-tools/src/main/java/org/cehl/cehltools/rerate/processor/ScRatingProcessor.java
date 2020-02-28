package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ScRatingProcessor implements RatingProcessor{

	RangeTable forwardRangeTable;
	RangeTable defenseRangeTable;
	
	public ScRatingProcessor() {
		initRanges();
	}
	
	void initRanges() {
		forwardRangeTable = new RangeTable();
		forwardRangeTable.insertValue(0, 60);
		forwardRangeTable.insertValue(0.1284, 70);
		forwardRangeTable.insertValue(0.28, 75);
		forwardRangeTable.insertValue(0.375, 80);
		forwardRangeTable.insertValue(0.6097, 99); //50 goals per season
		
		defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(0.08, 70);
		defenseRangeTable.insertValue(0.12, 74);
		defenseRangeTable.insertValue(0.18, 80);
		defenseRangeTable.insertValue(0.6097, 99); //50 goals per season
	}
	

	
	public int getRating2(Player player, PlayerStatAccumulator accumulator) {


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
		
		int lastRating = getRating3(last, player.getPosition());
		int last2Rating= getRating3(last2, player.getPosition());
		int last3Rating= getRating3(last3, player.getPosition());

		last2Rating = Math.max(last2Rating, 60);
		last3Rating = Math.max(last3Rating, 60);
		
		int totalRating = getRating3(totals, player.getPosition());
		

		
		 Map<Double, Integer> map = new HashMap<>();
		 
		 if(accumulator.getTotalSeasons() >= 4 && totalGroup.getGp() > 100) {
			  map.put((double) lastRating, 100);
			  map.put((double) last2Rating, 70);
			  map.put((double) last3Rating, 50);
			  map.put((double) totalRating, 50);
		 }else if(accumulator.getTotalSeasons() >= 3 && totalGroup.getGp() > 160) {
			  map.put((double) lastRating, 100);
			  map.put((double) last2Rating, 70);
			  map.put((double) last3Rating, 50);
		 }else if (accumulator.getTotalSeasons() >= 2 && totalGroup.getGp() > 100) {
			  map.put((double) lastRating, 100);
			  map.put((double) last2Rating, 70);
			  map.put((double) 60, 50);
		 }else {
			   //map.put((double) lastRating, 80);
			 map.put((double) totalRating, 80);
			   map.put((double) 60, 100);
		 }
		 
//		 if(accumulator.getTotalSeasons() == 1 && totalGroup.getGp() < 82) {
//			   map.put((double) lastRating, 80);
//			   map.put((double) 60, 100);
//		 }else if(accumulator.getTotalSeasons() >= 2 && totalGroup.getGp() < 164) {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) 60, 30);
//		 }else if (accumulator.getTotalSeasons() == 3) {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) last3Rating, 50);
//		 }else {
//			  map.put((double) lastRating, 100);
//			  map.put((double) last2Rating, 70);
//			  map.put((double) last3Rating, 50);
//			  map.put((double) totalRating, 50);
//		 }
	  
	     
	     Double weightedAverage = calculateWeightedAverage(map);

	   
	     Double weightedAverage2 = calculateWeightedAverage(map);
		
		if (totalGroup.getGp() < 20) {
			return Math.min(totalRating, 68);
		} else if (totalGroup.getGp() < 40) {
			return Math.min(totalRating, 70);
		} else if (totalGroup.getGp() < 55) {
			return Math.min(totalRating, 74);
		} else if (totalGroup.getGp() < 70) {
			return Math.min(totalRating, 77);
		} else if (accumulator.getAllSeasons().size() > 30) {
			return RerateUtils.normalizeRating(Math.max(weightedAverage, weightedAverage2));
		} else {
			return RerateUtils.normalizeRating(weightedAverage);
		}
		
	
		
		
		
	}
	
	public int getRating3(PlayerStatHolder stat, String position) {
		
		if(stat == null) return 0;
		
		double gp = (double)stat.getGp();
		double gpg = (double)stat.getGoals() / gp;

		double sc = 0;
		
		if(position.contains("D")) {
			//sc = (60 + (gpg * 110));
			sc = Double.valueOf(defenseRangeTable.findInterpolatedValue(gpg));
		}else {
			//sc = (60 + (gpg * 57));
			sc = Double.valueOf(forwardRangeTable.findInterpolatedValue(gpg));
		}
		
		return RerateUtils.normalizeRating(sc);
		
	}
	
	
	@Override
	public int getRating(PlayerSeason playerSeason) {

		Player player = playerSeason.getPlayer();
		
		double gp = (double)playerSeason.getStatsAll().getGp();
		double gpg = (double)playerSeason.getStatsAll().getGoals() / gp;

		double sc = 0;
		
		if(player.getPosition().contains("D")) {
			sc = (60 + (gpg * 110));
		}else {
			//sc = (60 + (gpg * 57));
			sc = Double.valueOf(forwardRangeTable.findInterpolatedValue(gpg));
		}
		
//		if("Auston Matthews".equalsIgnoreCase(player.getName()) || "Alex Ovechkin".equalsIgnoreCase(player.getName())){
//
//			System.out.println(String.valueOf(playerSeason.getStatsAll().getGoals()));
//			System.out.println(String.valueOf(gpg));
//			
//			System.out.println(RerateUtils.normalizeRating(sc));
//		}
		
		
		
//		if(gp < 20) {
//			sc = Math.min(sc, 75);
//		}else if(gp < 40) {
//			sc = Math.min(sc, 78);
//		}else if(gp < 60) {
//			sc = Math.min(sc, 83);
//		}

		return RerateUtils.normalizeRating(sc);
	}
	
	   /**
     * Calculates the weighted average of a map.
     * 
     *  Map<Double, Integer> map = new HashMap<>();
        map.put(0.7, 100);
        map.put(0.5, 200);
        Double weightedAverage = calculateWeightedAverage(map);
        Assert.assertTrue(weightedAverage.equals(0.5666666666666667));
     *
     * @throws ArithmeticException If divide by zero happens
     * @param map A map of values and weights
     * @return The weighted average of the map
     */
    static Double calculateWeightedAverage(Map<Double, Integer> map) throws ArithmeticException {
        double num = 0;
        double denom = 0;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            num += entry.getKey() * entry.getValue();
            denom += entry.getValue();
        }

        return num / denom;
    }

}
