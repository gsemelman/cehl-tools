package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public class ExRatingProcessor extends AbstractRatingProcessor{
	
	@Override
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		double gp = accumulator.getTotalStats().getGp();
		gp = Math.min(1000, gp); //only count up to 1000 gp.
		
		double age = RerateUtils.calculateAge(player.getDob());
		
		//double ex = (((age * 1.8) + gp) * 0.06) + 20;
		
		double ex = 0;
		
		if(player.getPosition().contains("G")) {
			ex = (gp * 0.065) + (age * 1.2);
		}else {
			ex = (gp * 0.055) + (age * 1.2);
		}

		ex = Math.max(ex, 35);

		return RerateUtils.normalizeRating(ex);
	}
	
//	@Override
//	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
//	
//		double gp = statHolder.getGp();
//		double age = RerateUtils.calculateAge(player.getDob());
//		
//		//double ex = (((age * 1.8) + gp) * 0.06) + 20;
//		
//		double ex = (gp * 0.055) + age;
//
//		return RerateUtils.normalizeRating(ex);
//	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		throw new UnsupportedOperationException("Unsupported opertation. Rating is performed based on career");
	}

}
