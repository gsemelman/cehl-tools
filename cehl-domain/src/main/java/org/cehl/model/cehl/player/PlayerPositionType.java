package org.cehl.model.cehl.player;

import java.util.ArrayList;
import java.util.List;

public enum PlayerPositionType {
	CENTER(0,"C","Center"),
	LEFT_WING(1,"LW","Left Wing"),
	RIGHT_WING(2,"RW","Right Wing"),
	DEFENSE(3,"D","Defense"),
	GOALIE(4,"G","Goalie");

	private final Integer rawValue;
	private final String stringValue;
	private final String description;
	
	PlayerPositionType(int rawValue, String stringValue, String description) {
		this.rawValue = rawValue;
		this.stringValue = stringValue;
		this.description = description;
	}
	
	public Integer rawValue() {
		return this.rawValue;
	}
	
	public String stringValue() {
		return this.stringValue;
	}

	public String description() {
		return this.description;
	}
	
	public boolean isForward() {
		return !PlayerPositionType.DEFENSE.equals(this) && !PlayerPositionType.GOALIE.equals(this);
	}
	
	public static List<PlayerPositionType> asList() {
		
		List<PlayerPositionType> flags = new ArrayList<PlayerPositionType>();

		flags.add(CENTER);
		flags.add(LEFT_WING);
		flags.add(RIGHT_WING);
		flags.add(DEFENSE);
		flags.add(GOALIE);
		
		return flags;
		
	}
	
	public static PlayerPositionType PositionByRawValue(int value) {
		for(PlayerPositionType ppt : asList()) {
			if(ppt.rawValue == value) {
				return ppt;
			}
		}
		
		throw new RuntimeException("Unable to parse raw position type value");
	}
	
	public static PlayerPositionType PositionByFlagValue(String stringValue) {
		for(PlayerPositionType ppt : asList()) {
			if(ppt.stringValue().equals(stringValue)) {
				return ppt;
			}
		}
		
		return null;
	}

}
