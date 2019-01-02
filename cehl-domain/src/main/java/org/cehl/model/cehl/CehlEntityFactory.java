package org.cehl.model.cehl;

import java.util.ArrayList;
import java.util.List;

import org.cehl.model.cehl.player.Player;
import org.cehl.model.cehl.player.PlayerAttributes;
import org.cehl.model.cehl.player.PlayerStatistics;

public class CehlEntityFactory {
	public static Player createPlayerInstance() {
		Player player = new Player();
		
		PlayerAttributes attributes = new PlayerAttributes();
		player.setAttributes(attributes);
		attributes.setPlayer(player);
		
		PlayerStatistics statistics = new PlayerStatistics();
		player.setStatistics(statistics);
		statistics.setPlayer(player);

		return player;
	}

}
