package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public class LdRatingProcessor extends ExRatingProcessor{
	
	@Override
	public double getRating(Player player, PlayerStatAccumulator accumulator) {
		
		double rating = super.getRating(player, accumulator);
		
		if(rating >= 88) {
			rating -= 5;
		}else if(rating >= 83) {
			rating -= 4;
		}else if(rating >= 78) {
			rating -= 3;
		}else {
			rating -= 2;
		}
		
		return rating;
	}
}
