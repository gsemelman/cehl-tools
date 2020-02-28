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
		forwardRangeTable.insertValue(14, 74);
		forwardRangeTable.insertValue(26, 92); //played all games
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		RangeTable defenseRangeTable = new RangeTable();
		defenseRangeTable.insertValue(0, 60);
		defenseRangeTable.insertValue(16, 70);
		defenseRangeTable.insertValue(30, 92); //played all games
		
		return defenseRangeTable;
	}
	

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = statHolder.getGp();
		double toi = statHolder.getToi();
		
		double toiPerGame = toi / gp;
		
		double du = 0;
		
		if(player.getPosition().contains("D")) {
			du = Double.valueOf(defenseRangeTable.findInterpolatedValue(toiPerGame));
		}else {
			du = Double.valueOf(forwardRangeTable.findInterpolatedValue(toiPerGame));
		}
		
		du = Math.max(du, 60);

		return RerateUtils.normalizeRating(du);
	}

}
