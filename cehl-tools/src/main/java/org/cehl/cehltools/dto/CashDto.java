package org.cehl.cehltools.dto;

public class CashDto {
	
	private String teamName;
	private Integer newCash;
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public Integer getNewCash() {
		return newCash;
	}
	public void setNewCash(Integer newCash) {
		this.newCash = newCash;
	}
	@Override
	public String toString() {
		return "CashDto [teamName=" + teamName + ", newCash=" + newCash + "]";
	}
	
}
