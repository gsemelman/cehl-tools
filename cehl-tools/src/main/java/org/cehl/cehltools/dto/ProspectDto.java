package org.cehl.cehltools.dto;

import org.cehl.raw.CehlTeam;

public class ProspectDto {
	private CehlTeam team;
	private String prospectName;

	public ProspectDto() {
		super();
	}

	public ProspectDto(CehlTeam team, String prospectName) {
		super();
		this.team = team;
		this.prospectName = prospectName;
	}
	
	public CehlTeam getTeam() {
		return team;
	}
	public void setTeam(CehlTeam team) {
		this.team = team;
	}
	public String getProspectName() {
		return prospectName;
	}
	public void setProspectName(String prospectName) {
		this.prospectName = prospectName;
	}

	@Override
	public String toString() {
		return "ProspectDto [team=" + team + ", prospectName=" + prospectName + "]";
	}




}
