package org.cehl.cehltools.dto;

import org.cehl.raw.Teams;

public class ProspectDto {
	private Teams team;
	private String prospectName;

	public ProspectDto() {
		super();
	}

	public ProspectDto(Teams team, String prospectName) {
		super();
		this.team = team;
		this.prospectName = prospectName;
	}
	
	public Teams getTeam() {
		return team;
	}
	public void setTeam(Teams team) {
		this.team = team;
	}
	public String getProspectName() {
		return prospectName;
	}
	public void setProspectName(String prospectName) {
		this.prospectName = prospectName;
	}




}
