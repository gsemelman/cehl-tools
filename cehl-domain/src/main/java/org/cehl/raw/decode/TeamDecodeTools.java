package org.cehl.raw.decode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cehl.raw.TeamRaw;

public class TeamDecodeTools {
private static int RECORD_LENGTH = 254;
	
	private List<TeamRaw> teamList;
	private int teamId = 0;
	
	public List<TeamRaw> loadTeams(File file) {

		List<byte[]> byteList = DecodeTools.decodeFile(file,RECORD_LENGTH);
		teamList = new ArrayList<TeamRaw>();
        
        for (byte[] bytes : byteList) {
        	TeamRaw team = new TeamRaw();

        	team.setTeamId(teamId);
        	//team.setTeamName(DecodeTools.readString(Arrays.copyOfRange(bytes,0, 10)).trim());
        	team.setTeamName(DecodeTools.readString2(bytes,0, 10));
        	//team.setGmName(DecodeTools.readString(Arrays.copyOfRange(bytes,10, 36)).trim());
        	team.setGmName(DecodeTools.readString2(bytes,10, 26));
        	//team.setGmIm(DecodeTools.readString(Arrays.copyOfRange(bytes,36, 61)).trim());
        	team.setGmIm(DecodeTools.readString2(bytes,36, 25));
        	//team.setTeamAbbrevation(DecodeTools.readString(Arrays.copyOfRange(bytes,61, 64)).trim());
        	team.setTeamAbbrevation(DecodeTools.readString2(bytes,61, 3));
        	//team.setStadiumName(DecodeTools.readString(Arrays.copyOfRange(bytes,64, 94)).trim());
        	team.setStadiumName(DecodeTools.readString2(bytes,64, 30));
        	team.setStadiumCapacity(DecodeTools.readShort(Arrays.copyOfRange(bytes,94, 96)));
        	team.setTicketPrice(DecodeTools.readByte(bytes[96]));
        	team.setConferenceId(DecodeTools.readByte(bytes[97]));
        	team.setDivisionId(DecodeTools.readByte(bytes[98]));
        	team.setTeamFinances(DecodeTools.readInt(Arrays.copyOfRange(bytes,99, 103)));
        	//team.setCoachName(DecodeTools.readString(Arrays.copyOfRange(bytes,103, 125)).trim());
        	team.setCoachName(DecodeTools.readString2(bytes,103, 22));
        	team.setCoachSalary(DecodeTools.readInt(Arrays.copyOfRange(bytes,125, 129)));
        	team.setCoachStyle(DecodeTools.readByte(bytes[129]));
        	team.setGoaliesLost(DecodeTools.readByte(bytes[130]));
        	team.setDefenceLost(DecodeTools.readByte(bytes[131]));
        	team.setTeamWins1(DecodeTools.readByte(bytes[132]));
        	team.setTeamWins2(DecodeTools.readByte(bytes[133]));
        	team.setTeamWins3(DecodeTools.readByte(bytes[134]));
        	team.setTeamLoses1(DecodeTools.readByte(bytes[135]));
        	team.setTeamLoses2(DecodeTools.readByte(bytes[136]));
        	team.setTeamLoses3(DecodeTools.readByte(bytes[137]));
        	team.setTeamTies1(DecodeTools.readByte(bytes[138]));
        	team.setTeamTies2(DecodeTools.readByte(bytes[139]));
        	team.setTeamTies3(DecodeTools.readByte(bytes[140]));
        	team.setTeamGoalsAllowed(DecodeTools.readShort(Arrays.copyOfRange(bytes,141, 143)));
        	team.setTeamGoalsFor(DecodeTools.readShort(Arrays.copyOfRange(bytes,143, 145)));
        	team.setTeamStreak(DecodeTools.readByte(bytes[145]));
        	team.setEliminated(DecodeTools.readByte(bytes[146]));
        	team.setRound1(DecodeTools.readByte(bytes[147]));
        	team.setExtra(DecodeTools.readByte(bytes[148]));
        	team.setFiller1(DecodeTools.readByte(bytes[149]));
        	team.setTeamFarmGoalsFor(DecodeTools.readShort(Arrays.copyOfRange(bytes,150, 152)));
        	team.setTeamFarmGoalsAgainst(DecodeTools.readShort(Arrays.copyOfRange(bytes,152, 154)));
        	team.setExtra2(DecodeTools.readByte(bytes[154]));
        	team.setTeamMoral(DecodeTools.readByte(bytes[155]));
        	team.setTeamPowerPlayAttempts(DecodeTools.readShort(Arrays.copyOfRange(bytes,156, 158)));
        	team.setTeamPowerPlayGoals(DecodeTools.readByte(bytes[158]));
        	team.setTeamPkAgainst(DecodeTools.readShort(Arrays.copyOfRange(bytes,159, 161)));
        	team.setTeamPkGoalsAllowed(DecodeTools.readByte(bytes[161]));
        	team.setTeamShotsFor(DecodeTools.readInt(Arrays.copyOfRange(bytes,162, 166)));
        	team.setTeamShotsAgainst(DecodeTools.readInt(Arrays.copyOfRange(bytes,166, 170)));
        	team.setTeamPenaltyMinutes(DecodeTools.readInt(Arrays.copyOfRange(bytes,170, 174)));
        	team.setL1c(DecodeTools.readByte(bytes[174]));
        	team.setL2c(DecodeTools.readByte(bytes[175]));
        	team.setL3c(DecodeTools.readByte(bytes[176]));
        	team.setL4c(DecodeTools.readByte(bytes[177]));
        	team.setPp51c(DecodeTools.readByte(bytes[178]));
        	team.setPp52c(DecodeTools.readByte(bytes[179]));
        	team.setPp41c(DecodeTools.readByte(bytes[180]));
        	team.setPp42c(DecodeTools.readByte(bytes[181]));
        	team.setPk41c(DecodeTools.readByte(bytes[182]));
        	team.setPk42c(DecodeTools.readByte(bytes[183]));
        	team.setPk31c(DecodeTools.readByte(bytes[184]));
        	team.setPk32c(DecodeTools.readByte(bytes[185]));
        	team.setG1(DecodeTools.readByte(bytes[186]));
        	team.setL1lw(DecodeTools.readByte(bytes[187]));
        	team.setL2lw(DecodeTools.readByte(bytes[188]));
        	team.setL3lw(DecodeTools.readByte(bytes[189]));
        	team.setL4lw(DecodeTools.readByte(bytes[190]));
        	team.setPp51lw(DecodeTools.readByte(bytes[191]));
        	team.setPp52lw(DecodeTools.readByte(bytes[192]));
        	team.setPp41lw(DecodeTools.readByte(bytes[193]));
        	team.setPp42lw(DecodeTools.readByte(bytes[194]));
        	team.setPk41lw(DecodeTools.readByte(bytes[195]));
        	team.setPk42lw(DecodeTools.readByte(bytes[196]));
        	team.setPk31d(DecodeTools.readByte(bytes[197]));
        	team.setPk32d(DecodeTools.readByte(bytes[198]));
        	team.setExtra(DecodeTools.readByte(bytes[199]));
        	team.setL1rw(DecodeTools.readByte(bytes[200]));
        	team.setL2rw(DecodeTools.readByte(bytes[201]));
        	team.setL3rw(DecodeTools.readByte(bytes[202]));
        	team.setL4rw(DecodeTools.readByte(bytes[203]));
        	team.setPp51rw(DecodeTools.readByte(bytes[204]));
        	team.setPp52rw(DecodeTools.readByte(bytes[205]));
        	team.setPp41d(DecodeTools.readByte(bytes[206]));
        	team.setPp42d(DecodeTools.readByte(bytes[207]));
        	team.setPk41d(DecodeTools.readByte(bytes[208]));
        	team.setPk42d(DecodeTools.readByte(bytes[209]));
        	team.setPk31d2(DecodeTools.readByte(bytes[210]));
        	team.setPk32d2(DecodeTools.readByte(bytes[211]));
        	team.setExtra2(DecodeTools.readByte(bytes[212]));
        	team.setL1d(DecodeTools.readByte(bytes[213]));
        	team.setL2d(DecodeTools.readByte(bytes[214]));
        	team.setL3d(DecodeTools.readByte(bytes[215]));
        	team.setL4d(DecodeTools.readByte(bytes[216]));
        	team.setPp51d(DecodeTools.readByte(bytes[217]));
        	team.setPp52d(DecodeTools.readByte(bytes[218]));
        	team.setPp41d2(DecodeTools.readByte(bytes[219]));
        	team.setPp42d2(DecodeTools.readByte(bytes[220]));
        	team.setPk41d2(DecodeTools.readByte(bytes[221]));
        	team.setPk42d2(DecodeTools.readByte(bytes[222]));
        	team.setAuto1(DecodeTools.readByte(bytes[223]));
        	team.setAuto2(DecodeTools.readByte(bytes[224]));
        	team.setAuto3(DecodeTools.readByte(bytes[225]));
        	team.setL1d2(DecodeTools.readByte(bytes[226]));
        	team.setL2d2(DecodeTools.readByte(bytes[227]));
        	team.setL3d2(DecodeTools.readByte(bytes[228]));
        	team.setL4d2(DecodeTools.readByte(bytes[229]));
        	team.setPp51d2(DecodeTools.readByte(bytes[230]));
        	team.setPp52d2(DecodeTools.readByte(bytes[231]));
        	team.setAvgatt(DecodeTools.readShort(Arrays.copyOfRange(bytes,232, 234)));
        	team.setGroups(DecodeTools.readByte(bytes[234]));
        	team.setAvgsales(DecodeTools.readInt(Arrays.copyOfRange(bytes,235, 239)));
        	team.setPriority(DecodeTools.readByte(bytes[239]));
        	team.setClaim(DecodeTools.readByte(bytes[240]));
        	team.setCoachref(DecodeTools.readByte(bytes[249]));
        	team.setDraftorder(DecodeTools.readByte(bytes[250]));
        	team.setExpansion(DecodeTools.readByte(bytes[252]));
        	team.setLinesdone(DecodeTools.readByte(bytes[253]));
        	
    
        	System.out.println(team.toString());
        	
        	
//        	System.out.println(team.getTeamName());
//        	System.out.println(team.getGmName());
//        	System.out.println(team.getGmIm());
//        	System.out.println(team.getTeamAbbrevation());
//        	System.out.println(team.getStadiumName());
//        	System.out.println(team.getStadiumCapacity());
//        	System.out.println(team.getTicketPrice());
//        	System.out.println(team.getConferenceId());
//        	System.out.println(team.getDivisionId());
//        	System.out.println(team.getTeamFinances());
//        	System.out.println(team.getCoachName());
//        	System.out.println(team.getCoachSalary());
        	
        	teamList.add(team);

        	teamId++;
        }
        
        return teamList;
	}
}
