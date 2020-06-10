package org.cehl.cehltools.dto;

import org.cehl.raw.CehlTeam;

public class DraftPickDto {
	CehlTeam team;
	int year;
	int selection;
	int teamId;
	int newTeamId;
	int active;
	

	public CehlTeam getTeam() {
		return team;
	}
	public void setTeam(CehlTeam team) {
		this.team = team;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getSelection() {
		return selection;
	}
	public void setSelection(int selection) {
		this.selection = selection;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getNewTeamId() {
		return newTeamId;
	}
	public void setNewTeamId(int newTeamId) {
		this.newTeamId = newTeamId;
	}
	
	//0 = no, -1 = year
	public int isActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	@Override
	public String toString() {
		return "DraftPickDto [team=" + team + ", year=" + year + ", selection=" + selection + ", teamId=" + teamId
				+ ", newTeamId=" + newTeamId + ", active=" + active + "]";
	}

	
	
}
