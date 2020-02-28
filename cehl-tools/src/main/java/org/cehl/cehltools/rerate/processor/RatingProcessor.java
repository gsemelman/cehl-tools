package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.model.PlayerSeason;

public interface RatingProcessor {
	public int getRating(PlayerSeason playerSeason);
	
	public default int getMinRating() {
		return 25;
	}
	
	public default int getMaxRating() {
		return 99;
	}
}
 