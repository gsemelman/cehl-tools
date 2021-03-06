package org.cehl.commons;

public enum SimFileType {
	
	COACH_FILE("coa"),
	DRS_FILE("drs"),
	ROSTER_FILE("ros"),
	TEAM_FILE("tms"),
	PROSPECT_FILE("pct"),
	DRAFT_PICK_FILE("dpk");

	private String extension;

	private SimFileType(String extension) {
		this.extension = extension;

	}
	
    public String getExtension() {
    	return extension;
    }

}
