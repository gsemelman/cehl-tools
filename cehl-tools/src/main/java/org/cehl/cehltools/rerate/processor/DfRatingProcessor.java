package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;

public class DfRatingProcessor extends AbstractRatingProcessor{

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;

		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = (60 + (apg * 52));
		}else {
			pa = (60 + (apg * 42));
		}

		return RerateUtils.normalizeRating(pa);
	}

}
