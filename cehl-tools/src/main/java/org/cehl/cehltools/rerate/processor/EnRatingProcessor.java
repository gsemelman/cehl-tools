package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class EnRatingProcessor extends AbstractRatingProcessor{
	
	static RangeTable forwardRangeTable = initForwardRanges();
	static RangeTable defenseRangeTable = initDefenseRanges();
	

	static RangeTable initForwardRanges() {
		RangeTable forwardRangeTable = new RangeTable();
		forwardRangeTable.insertValue(0, 60);
		forwardRangeTable.insertValue(14, 75);
		forwardRangeTable.insertValue(25, 90); //played all games
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(14, 72);
		defenseRangeTable.insertValue(18, 78);
		defenseRangeTable.insertValue(31, 90); //played all games
		
		return defenseRangeTable;
	}
	

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = statHolder.getGp();
		double toi = statHolder.getToi();
		
		double toiPerGame = toi / gp;
		
		double en = 0;
		
		if(player.getPosition().contains("D")) {
			en = Double.valueOf(defenseRangeTable.findInterpolatedValue(toiPerGame));
		}else {
			en = Double.valueOf(forwardRangeTable.findInterpolatedValue(toiPerGame));
		}
		
		en = Math.max(en, 60);

		return RerateUtils.normalizeRating(en);
	}

}
