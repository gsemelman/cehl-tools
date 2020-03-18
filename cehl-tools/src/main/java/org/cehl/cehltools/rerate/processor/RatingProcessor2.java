package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.agg.PlayerStatAccumulator;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.rating.RatingResult;

public interface RatingProcessor2 {
	public double getRating(Player player, PlayerStatAccumulator accumulator);
	public int getSeasonRating(Player player, PlayerStatHolder statHolder);
	public default void postProcess(RatingResult ratingResult) {
		return;
	}
}
 