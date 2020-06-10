package org.cehl.cehltools.dto;

import org.cehl.raw.CehlTeam;

public class TeamPlayerDto {
	private CehlTeam team;
	private String playerName;

	public TeamPlayerDto() {
		super();
	}
	
	public TeamPlayerDto(CehlTeam team, String playerName) {
		super();
		this.team = team;
		this.playerName = playerName;
	}

	public CehlTeam getTeam() {
		return team;
	}

	public void setTeam(CehlTeam team) {
		this.team = team;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	
	
	
}
