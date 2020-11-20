package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;

public class DfRatingProcessor extends AbstractRatingProcessor{

	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double blocks = (double)statHolder.getShotsBlocked() / gp;
		double shtoi = (double)statHolder.getPkToi() / gp;
		double tkaway = statHolder.getTakeAway() / gp;
		
		if(Double.isNaN(blocks)) blocks = 0;
		if(Double.isNaN(shtoi)) shtoi = 0;
		if(Double.isNaN(tkaway)) tkaway = 0;
		
	//	Forward: ((BkS / GP) x 2.5) + (TkA x 0.5) + ((SHTOI / GP) x 7.5) + (DP x 0.1) + 20
	

		//double df = (blocks * 2.5) + (tkaway * 2.5) + (shtoi * 7.5) + 65;
		
		double df = Math.max((blocks * 1.5),(tkaway * 1.5)) + (shtoi * 5) + 65;
		
		df = Math.min(85, df);

		return RerateUtils.normalizeRating(df);
	}

}
