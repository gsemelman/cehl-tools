package org.cehl.raw;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum Teams {
	ANA(0,"Anaheim"),
	WPG(1,"Winnipeg"),
	BOS(2,"Boston"),
	BUF(3,"Buffalo"),
	CGY(4,"Calgary"),
	CAR(5,"Carolina"),
	CHI(6,"Chicago"),
	COL(7,"Colorado"),
	DAL(8,"Dallas"),
	DET(9,"Detroit"),
	EDM(10,"Edmonton"),
	FLA(11,"Florida"),
	LA(12,"LosAngeles"),
	MTL(13,"Montreal"),
	NAS(14,"Nashville"),
	NJ(15,"New Jersey"), //New Jersey
	NYI(16,"Islanders"),
	NYR(17,"Rangers"),
	OTT(18,"Ottawa"),
	PHI(19,"Philly"),
	PHO(20,"Phoenix"),
	PIT(21,"Pittsburgh"),
	STL(22,"St. Louis"), //St. Louis
	SJ(23,"San Jose"), //San Jose
	TB(24,"Tampa Bay"), //Tampa Bay
	TOR(25,"Toronto"),
	VAN(26,"Vancouver"),
	WAS(27,"Washington"),
	CBJ(28,"Columbus"),
	MIN(29,"Minnesota");
	
	Integer teamId;
	String name;
	
	public Integer getTeamId(){
		return teamId;
	}
	
	public String getName(){
		return name;
	}
	
	Teams(int teamId, String name){
		this.teamId = teamId;
		this.name= name;
	}
	
	public static Teams fromId(int code) {
		for(Teams team : Teams.values()) {
			if(team.getTeamId() == code) {
				return team;
			}
		}
		return null;
	}
	
	public static Teams fromAbbr(String abbr) {
		for(Teams team : Teams.values()) {
			if(team.name().equals(abbr)) {
				return team;
			}
		}
		return null;
	}
	
	public static Teams fromName(String name) {
		for(Teams team : Teams.values()) {
			if(team.getName().equals(name)) {
				return team;
			}
		}
		return null;
	}
	
	public static Set<String> getMissingTeamsByAbbr(Collection<String> abbr){
		Set<String> missingAbbr = new HashSet<String>();
		
		for(Teams team : Teams.values()) {
			if(!abbr.contains(team.name())){
				missingAbbr.add(team.getName());
			}
		}
		
		return missingAbbr;
	}
	
}