package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class PaRatingProcessor extends AbstractRatingProcessor{

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

		defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(0.285714286, 75);
		defenseRangeTable.insertValue(0.8760, 97); //played all games
		
		return defenseRangeTable;
	}
	

	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;

		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = Double.valueOf(defenseRangeTable.findInterpolatedValue(apg)) * 1.045;
		}else {
			pa = Double.valueOf(forwardRangeTable.findInterpolatedValue(apg));
		}
		
		pa = Math.min(pa, 97);

		return RerateUtils.normalizeRating(pa);
	}

}
