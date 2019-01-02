package org.cehl.raw.decode;

import org.cehl.model.cehl.player.PlayerHandType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.Teams;

public class ValidationHelper {
	
	public static int ATTRIB_BASIC_MIN_VALUE = 55;
	public static int ATTRIB_BASIC__MAX_VALUE = 99;
	
	public static int ATTRIB_MIN_SALARY = 400000;
	public static int ATTRIB_MAX_SALARY = 15000000;
	
	public static int PLAYER_NAME_MIN_LEN = 1;
	public static int PLAYER_NAME_MAX_LEN = 22;
	
	public static int PLAYER_WEIGHT_MIN = 140;
	public static int PLAYER_WEIGHT_MAX = 255;
	
	public static int PLAYER_HEIGHT_MIN = 60; //5'0
	public static int PLAYER_HEIGHT_MAX = 96; //8'0
	
	public static int PLAYER_AGE_MIN = 18; 
	public static int PLAYER_AGE_MAX = 55; 
	
	public static int PLAYER_CONTRACT_MIN = 1; 
	public static int PLAYER_CONTRACT_MAX = 5; 
	
	
	
	public static boolean validateBasicAttribute(int attribute){
		return (attribute >= ATTRIB_BASIC_MIN_VALUE) && (attribute <= ATTRIB_BASIC__MAX_VALUE);
	}
	
	public static boolean validateSalary(int attribute){
		return (attribute >= ATTRIB_MIN_SALARY) && (attribute <= ATTRIB_MAX_SALARY);
	}
	
	public static boolean validateContractLength(int contract){
		return (contract >= PLAYER_CONTRACT_MIN) && (contract <= PLAYER_CONTRACT_MAX);
	}
	
	public static boolean validatePlayerName(String name){
		return (name != null) && (name.length() >= PLAYER_NAME_MIN_LEN) && (name.length() <= PLAYER_NAME_MAX_LEN);
	}
	
	public static boolean validatePlayerHand(String handCode){
		return PlayerHandType.HandTypeByFlagValue(handCode) != null;
	}
	
	public static boolean validatePlayerPosition(String positionCode){
		return PlayerPositionType.PositionByFlagValue(positionCode) != null;
	}
	
	public static boolean validatePlayerAge(int age){
		return (age >= PLAYER_AGE_MIN) && (age <= PLAYER_AGE_MAX);
	}
	
	public static boolean validatePlayerHeight(int height){
		return (height >= PLAYER_HEIGHT_MIN) && (height <= PLAYER_HEIGHT_MAX);
	}
	
	public static boolean validateTeamAbbreviation(String abbr){
		Teams team = Teams.fromAbbr(abbr);
		return team != null;
	}
	

}
