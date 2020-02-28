package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;

public interface RatingProcessor2 {
	public double getRating(Player player, PlayerStatAccumulator accumulator);
	public int getSeasonRating(Player player, PlayerStatHolder statHolder);
	
	public default int getMinRating() {
		return 25;
	}
	
	public default int getMaxRating() {
		return 99;
	}
}
 