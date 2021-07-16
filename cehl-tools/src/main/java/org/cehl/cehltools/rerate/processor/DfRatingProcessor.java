package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class DfRatingProcessor extends AbstractRatingProcessor{
	
	static RangeTable xgfpercentRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 50);
		rangeTable.insertValue(47, 65);
		rangeTable.insertValue(50, 70);
		rangeTable.insertValue(55, 80); 
		rangeTable.insertValue(65, 85); 
		
		return rangeTable;

	}
	
	static RangeTable pkRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(0.5, 65);
		rangeTable.insertValue(2, 75);
		rangeTable.insertValue(2.5, 80); 
		rangeTable.insertValue(3.4, 90); 
		
		return rangeTable;

	}
	
	static RangeTable takeAwayRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(0.25, 70);
		rangeTable.insertValue(0.47, 75);
		rangeTable.insertValue(0.55, 80); 
		rangeTable.insertValue(1, 90); 
		
		return rangeTable;

	}
	
	//average 1.25
	static RangeTable shotBlockRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(1, 70);
		rangeTable.insertValue(1.5, 75);
		rangeTable.insertValue(1.7, 80); 
		rangeTable.insertValue(2.7, 90); 
		
		return rangeTable;

	}
	
	static RangeTable esToiRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 67);
		rangeTable.insertValue(18, 70);
		rangeTable.insertValue(20, 75);
		rangeTable.insertValue(24, 80); 
		rangeTable.insertValue(26, 90); 
		
		return rangeTable;

	}

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double blocks = (double)statHolder.getShotsBlocked() / gp;
		double shtoi = (double)statHolder.getPkToi() / gp;
		double tkaway = statHolder.getTakeAway() / gp;
		double xgfPercent = statHolder.getXgfPct();
		double toiPerGame = statHolder.getToi() / gp;
		
		if(Double.isNaN(gp)) gp = 0;
		if(Double.isNaN(blocks)) blocks = 0;
		if(Double.isNaN(shtoi)) shtoi = 0;
		if(Double.isNaN(tkaway)) tkaway = 0;

		double xgfPctRating = Double.valueOf(xgfpercentRange().findInterpolatedValue(xgfPercent));
		double pkRating = Double.valueOf(pkRange().findInterpolatedValue(shtoi));
		double tkAwayRating = Double.valueOf(pkRange().findInterpolatedValue(shtoi));
		double shotBlockRating = Double.valueOf(shotBlockRange().findInterpolatedValue(blocks));
		double esDfRating = Double.valueOf(esToiRange().findInterpolatedValue(toiPerGame)); //* 1.045;
		
		//df = (df + xgfPctRating) / 2;
		Double df = null;
		if(player.getPosition().contains("D")) {
			
			xgfPctRating = Math.max(68, xgfPctRating);
			pkRating = Math.max(65, pkRating);
			tkAwayRating = Math.max(68, tkAwayRating);
			shotBlockRating = Math.max(68, shotBlockRating);
			esDfRating = Math.max(70, esDfRating);
			
			Map<Double, Integer> map = new HashMap<>();

			RerateUtils.addToAverageMap(map,pkRating,100);
			RerateUtils.addToAverageMap(map,xgfPctRating,100);
			RerateUtils.addToAverageMap(map,esDfRating,100);
			
			RerateUtils.addToAverageMap(map,Math.max(tkAwayRating, shotBlockRating),100);
			
			df = RerateUtils.calculateWeightedAverage(map);

		}else {
			Map<Double, Integer> map = new HashMap<>();
			//want to adjust for defensive players who may not have a high xgf%
			if(pkRating > xgfPctRating) {
				RerateUtils.addToAverageMap(map,pkRating,100);
				RerateUtils.addToAverageMap(map,xgfPctRating,50);
				RerateUtils.addToAverageMap(map,Math.max(tkAwayRating, shotBlockRating),75);
			}else {
				RerateUtils.addToAverageMap(map,pkRating,50);
				RerateUtils.addToAverageMap(map,xgfPctRating,100);
				RerateUtils.addToAverageMap(map,Math.max(tkAwayRating, shotBlockRating),75);
				
			}
			
			//Map<Double, Integer>  map2 = new HashMap<Double, Integer>(map);
			
			//RerateUtils.addToAverageMap(map,Math.max(tkAwayRating, shotBlockRating),50);
			
			df = RerateUtils.calculateWeightedAverage(map);
		}

		//df = Math.min(85, df);
		df = Math.max(60, df);
		return RerateUtils.normalizeRating(df);
	}
	
	public void postProcess(RatingResult ratingResult) {
		
		if(!ratingResult.getPlayer().getPosition().contains("D")) return;

		int st = RerateUtils.roundUpToInt(ratingResult.getSt());
		double df = ratingResult.getDf();
		if(st >80) {
			df +=4;
		}else if(st > 77) {
			df +=3;
		}else if(st > 73) {
			df +=1;
		}
		ratingResult.setDf(df);
		
		return;
	}

}
