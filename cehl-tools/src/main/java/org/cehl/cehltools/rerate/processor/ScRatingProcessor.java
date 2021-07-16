package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ScRatingProcessor extends AbstractRatingProcessor{

	static RangeTable forwardRangeTable = initForwardRanges();
	static RangeTable defenseRangeTable = initDefenseRanges();
	

	static RangeTable initForwardRanges() {
		RangeTable forwardRangeTable = new RangeTable();
//		forwardRangeTable.insertValue(0, 60);
//		forwardRangeTable.insertValue(0.1219, 70);
//		forwardRangeTable.insertValue(0.243, 74);
//		forwardRangeTable.insertValue(0.375, 80); //30 gps
//		forwardRangeTable.insertValue(0.45, 83);
//		forwardRangeTable.insertValue(0.6097, 94); //50 goals per season
		
		forwardRangeTable.insertValue(0, 60);
		forwardRangeTable.insertValue(0.125, 70); //10 gps
		forwardRangeTable.insertValue(0.25, 74);
		forwardRangeTable.insertValue(0.375, 80); //30 gps
		forwardRangeTable.insertValue(0.45, 83);
		forwardRangeTable.insertValue(0.6097, 93); //50 goals per season


		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();

		defenseRangeTable.insertValue(0.01, 62);
		//defenseRangeTable.insertValue(0.055, 70);
		defenseRangeTable.insertValue(0.055, 69.5);
		//defenseRangeTable.insertValue(0.12, 74);
		defenseRangeTable.insertValue(0.12, 73);
		//defenseRangeTable.insertValue(0.1951, 81);
		defenseRangeTable.insertValue(0.1951, 80);
		defenseRangeTable.insertValue(0.24, 84);
		
		

		
		return defenseRangeTable;
	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		
		double gp = (double)statHolder.getGp();
		double gpg = (double)statHolder.getGoals() / gp;

		double sc = 0;
		
		if(player.getPosition().contains("D")) {
			//sc = (60 + (gpg * 110));
			sc = Double.valueOf(defenseRangeTable.findInterpolatedValueSmooth(gpg));
		}else {
			//sc = (60 + (gpg * 57));
			sc = Double.valueOf(forwardRangeTable.findInterpolatedValueSmooth(gpg));
		}
		
		return RerateUtils.normalizeRating(sc);
	}






}
