package org.cehl.raw;

import java.util.Objects;

public class DpkRaw {
	
	private int teamId;
	private int yearIndex;
	private int selection;
	private int pickTeamId;
	private int filler;
	private int group1;
	private int group2;
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getYearIndex() {
		return yearIndex;
	}
	public void setYearIndex(int yearIndex) {
		this.yearIndex = yearIndex;
	}
	public int getSelection() {
		return selection;
	}
	public void setSelection(int selection) {
		this.selection = selection;
	}
	public int getPickTeamId() {
		return pickTeamId;
	}
	public void setPickTeamId(int pickTeamId) {
		this.pickTeamId = pickTeamId;
	}
	public int getFiller() {
		return filler;
	}
	public void setFiller(int filler) {
		this.filler = filler;
	}
	public int getGroup1() {
		return group1;
	}
	public void setGroup1(int group1) {
		this.group1 = group1;
	}
	public int getGroup2() {
		return group2;
	}
	public void setGroup2(int group2) {
		this.group2 = group2;
	}
	@Override
	public int hashCode() {
		return Objects.hash(filler, group1, group2, pickTeamId, selection, teamId, yearIndex);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DpkRaw other = (DpkRaw) obj;
		return filler == other.filler && group1 == other.group1 && group2 == other.group2
				&& pickTeamId == other.pickTeamId && selection == other.selection && teamId == other.teamId
				&& yearIndex == other.yearIndex;
	}
	

	

	
}
