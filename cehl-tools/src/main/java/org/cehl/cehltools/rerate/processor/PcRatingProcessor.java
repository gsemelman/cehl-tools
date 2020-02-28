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
		forwardRangeTable.insertValue(0, 60);
		forwardRangeTable.insertValue(0.285714286, 75);
		forwardRangeTable.insertValue(0.8760, 97); //played all games
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(0.285714286, 75);
		defenseRangeTable.insertValue(0.8760, 97); //played all games
		
		return defenseRangeTable;
	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;
		double cfPct = statHolder.getCfPct();
		double ppToi =  statHolder.getPpToi();
		double ppPoints =  statHolder.getPpPoints();
		
		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = Double.valueOf(defenseRangeTable.findInterpolatedValue(apg)) * 1.045;
		}else {
			pa = Double.valueOf(forwardRangeTable.findInterpolatedValue(apg));
		}
		
		pa = pa - 10;
		
		double cfBonus = 0;		
		if(cfPct >= 57) {
			cfBonus = 10;
		}else if(cfPct >= 57) {
			cfBonus = 8;
		}else if(cfPct >= 55) {
			cfBonus = 6;
		}else if(cfPct >= 53) {
			cfBonus = 4.5;
		}else if(cfPct >= 51) {
			cfBonus = 3;
		}else if(cfPct >= 50) {
			cfBonus = 0;
		}else if(cfPct >= 49) {
			cfBonus = -3;
		}else if(cfPct >= 47) {
			cfBonus = -4;
		}
		
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
			ppBonus = Math.max(pppm, 7);
		}
		
		double pc = pa + cfBonus + ppBonus;

		return RerateUtils.normalizeRating(pc);
	}

}
