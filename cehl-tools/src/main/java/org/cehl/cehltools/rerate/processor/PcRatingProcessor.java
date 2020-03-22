package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class PcRatingProcessor extends AbstractRatingProcessor{
	
	static RangeTable forwardRangeTable = initForwardRanges();
	static RangeTable defenseRangeTable = initDefenseRanges();
	
	static RangeTable initForwardRanges() {
		RangeTable forwardRangeTable = new RangeTable();
		forwardRangeTable.insertValue(0.05, 60);
		forwardRangeTable.insertValue(0.1219, 70);
		forwardRangeTable.insertValue(0.2857, 75);
		//forwardRangeTable.insertValue(0.8760, 97); 
		forwardRangeTable.insertValue(0.8760, 89);
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();
//		defenseRangeTable.insertValue(0.05, 60);
//		defenseRangeTable.insertValue(0.1, 69);
//		defenseRangeTable.insertValue(0.25, 76);
//		defenseRangeTable.insertValue(0.55, 80.5);	
//		defenseRangeTable.insertValue(0.8760, 88.5); 
		
		defenseRangeTable.insertValue(0.05, 60);
		defenseRangeTable.insertValue(0.1, 69.5);
		defenseRangeTable.insertValue(0.25, 76.5);
		defenseRangeTable.insertValue(0.55, 81.5);	
		defenseRangeTable.insertValue(0.8760, 90); 
		
		return defenseRangeTable;
	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;
		double gpg = (double)statHolder.getGoals() / gp;
		double cfPct = statHolder.getCfPct();
		double ppToi =  statHolder.getPpToi();
		double ppPoints =  statHolder.getPpPoints();
		
		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = Double.valueOf(defenseRangeTable.findInterpolatedValue(apg)); //* 1.045;
		}else {
			pa = Double.valueOf(forwardRangeTable.findInterpolatedValue(apg));
		}

	
		if(pa > 81) {
			pa-=4;
		}else if(pa > 77) {
			pa-=3;
		}else if(pa > 70) {
			pa-=2;
		}else {
			pa-=1;
		}

		
		//calculate corsi cf% bonus
		double cfBonus = 0;		
		if(cfPct >= 58) {
			cfBonus = 6;
		}else if(cfPct >= 57) {
			cfBonus = 5;
		}else if(cfPct >= 55) {
			cfBonus = 4;
		}else if(cfPct >= 53) {
			cfBonus = 2;
		}else if(cfPct >= 51) {
			cfBonus = 1.5;
		}else if(cfPct >= 50) {
			cfBonus = 1;
		}else if(cfPct >= 49) {
			cfBonus = -2;
		}else if(cfPct >= 47) {
			cfBonus = -3;
		}
		
		//calculate powerplay points bonus
		double pppm = 0;
		if(ppToi > 0 && ppPoints > 0) {
			try {
				pppm = ppPoints / (ppToi / 60);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
			
		}
		
		double ppBonus = 0;	
		if(statHolder.getPpToi() > 40) {
			ppBonus = Math.min(pppm, 5);
		}
		
		double goalBonus = 0;

		//calculate goal bonus for forwards
		if(!player.getPosition().contains("D")) {
			if(gpg >= 0.45) {
				goalBonus = 3;
			}else if(gpg >= 0.45) {
				goalBonus = 2;
			}else if(gpg >= 0.4) {
				goalBonus = 1.5;
			}else if(gpg >= 0.3048) {
				goalBonus = 1;
			}
		}
		
		//double totalBonus = Math.min(cfBonus + goalBonus + ppBonus, 9);
		double totalBonus = Math.min(cfBonus + goalBonus + ppBonus, 11);
		 
		double pc = pa + totalBonus;
		

		return RerateUtils.normalizeRating(pc);
	}

}
