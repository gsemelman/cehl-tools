package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public class DiRatingProcessor extends AbstractRatingProcessor{


	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double toi = statHolder.getToi();
		double pim = statHolder.getPim();
		
		double pimPerMin = pim/toi;

		double di;
		
		di = (89 - (pimPerMin / 0.00141));
		
		//min of 45 di
		di = Math.max(di, 45);

		return RerateUtils.normalizeRating(di);
	}

}
