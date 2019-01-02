package org.cehl.model.cehl.player;

import java.util.ArrayList;
import java.util.List;

public enum PlayerHandType {
	LEFT(0,"L","Left Handed"),
	RIGHT(1,"R", "Right Handed");

	private final int rawValue;
	private final String stringValue;
	private final String description;
	
	PlayerHandType(int rawValue, String stringValue, String description) {
		this.rawValue = rawValue;
		this.stringValue = stringValue;
		this.description = description;
	}

	public int rawValue() {
		return this.rawValue;
	}
	
	public String stringValue() {
		return this.stringValue;
	}

	public String description() {
		return this.description;
	}
	
	public static List<PlayerHandType> asList() {
		
		List<PlayerHandType> flags = new ArrayList<PlayerHandType>();

		flags.add(LEFT);
		flags.add(RIGHT);
		
		return flags;	
	}
	
	public static PlayerHandType HandTypeByRawValue(int value) {
		for(PlayerHandType pht : asList()) {
			if(pht.rawValue == value) {
				return pht;
			}
		}
		
		return null;
	}
	
	public static PlayerHandType HandTypeByFlagValue(String stringValue) {
		for(PlayerHandType pht : asList()) {
			if(pht.stringValue().equals(stringValue)) {
				return pht;
			}
		}
		
		return null;
	}
}
