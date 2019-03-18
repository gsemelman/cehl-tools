package org.cehl.cehltools.dto;

import org.cehl.raw.Teams;

public class TeamPlayerDto {
	private Teams team;
	private String playerName;

	public TeamPlayerDto() {
		super();
	}
	
	public TeamPlayerDto(Teams team, String playerName) {
		super();
		this.team = team;
		this.playerName = playerName;
	}

	public Teams getTeam() {
		return team;
	}

	public void setTeam(Teams team) {
		this.team = team;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	
	
	
}
