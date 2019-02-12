package org.cehl.cehltools.dto;

public class DraftPickDto {
	int year;
	int selection;
	int teamId;
	int newTeamId;
	
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DraftPickDto [year=");
		builder.append(year);
		builder.append(", selection=");
		builder.append(selection);
		builder.append(", teamId=");
		builder.append(teamId);
		builder.append(", newTeamId=");
		builder.append(newTeamId);
		builder.append("]");
		return builder.toString();
	}
	
	
}
