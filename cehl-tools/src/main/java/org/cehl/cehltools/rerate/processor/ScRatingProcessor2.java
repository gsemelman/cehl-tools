package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class ScRatingProcessor2 extends AbstractRatingProcessor{

	static RangeTable forwardRangeTable = initForwardRanges();
	static RangeTable defenseRangeTable = initDefenseRanges();
	

	static RangeTable initForwardRanges() {
		RangeTable forwardRangeTable = new RangeTable();
		forwardRangeTable.insertValue(0, 60);
		forwardRangeTable.insertValue(0.1284, 70);
		forwardRangeTable.insertValue(0.28, 75);
		forwardRangeTable.insertValue(0.375, 80);
		//forwardRangeTable.insertValue(0.6097, 99); //50 goals per season
		forwardRangeTable.insertValue(0.45, 83);
		forwardRangeTable.insertValue(0.6097, 99); //50 goals per season
		//forwardRangeTable.insertValue(0.43, 80);
		//forwardRangeTable.insertValue(0.7, 95); //50 goals per season
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(0.08, 70);
		defenseRangeTable.insertValue(0.12, 74);
		defenseRangeTable.insertValue(0.18, 80);
		defenseRangeTable.insertValue(0.6, 99); //50 goals per season
		
		return defenseRangeTable;
	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		if(statHolder == null) return 0;
		
		double gp = (double)statHolder.getGp();
		double gpg = (double)statHolder.getGoals() / gp;

		double sc = 0;
		
		if(player.getPosition().contains("D")) {
			//sc = (60 + (gpg * 110));
			sc = Double.valueOf(defenseRangeTable.findInterpolatedValue(gpg));
		}else {
			//sc = (60 + (gpg * 57));
			sc = Double.valueOf(forwardRangeTable.findInterpolatedValue(gpg));
		}
		
		return RerateUtils.normalizeRating(sc);
	}






}
