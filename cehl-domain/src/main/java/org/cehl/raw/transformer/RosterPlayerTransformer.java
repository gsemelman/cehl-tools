package org.cehl.raw.transformer;

import org.cehl.model.cehl.CehlEntityFactory;
import org.cehl.model.cehl.player.Player;
import org.cehl.model.cehl.player.PlayerHandType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.model.cehl.player.PlayerStatistics;
import org.cehl.raw.RosterRaw;

public class RosterPlayerTransformer {
	
	
	public Player transformRoster(RosterRaw roster) {
		
		Player player = CehlEntityFactory.createPlayerInstance();
		
		PlayerPositionType positionType = PlayerPositionType.PositionByRawValue(roster.getPosition());
		PlayerHandType handType = PlayerHandType.HandTypeByRawValue(roster.getHand());
		
		player.setTeamPlayerId(roster.getTeamPlayerId());
		
		player.setPlayerName(roster.getName());
		
		player.setAge(roster.getAge());
		player.setBirthPlace(roster.getBirthPlace());
		player.setCondition(roster.getCondition());
		player.setHandType(handType.stringValue());
		player.setHeight(roster.getHeight());
		player.setInjuryStatus(roster.getInjStatus());
		player.setJerseyNumber(roster.getJersey());
		player.setPosition(positionType.stringValue());
		player.setSalary(roster.getSalary());
		player.setWeight(roster.getWeight());
		
		player.getAttributes().setDefense(roster.getDf());
		player.getAttributes().setDiscipline(roster.getDi());
		player.getAttributes().setDuribility(roster.getDu());
		player.getAttributes().setEndurence(roster.getEn());
		player.getAttributes().setExperience(roster.getEx());
		player.getAttributes().setIntensity(roster.getIt());
		player.getAttributes().setLeadership(roster.getLd());
		player.getAttributes().setPassAccuracy(roster.getPa());
		player.getAttributes().setPuckControl(roster.getPc());
		player.getAttributes().setScoring(roster.getSc());
		player.getAttributes().setSkating(roster.getSk());
		player.getAttributes().setSpeed(roster.getSp());
		player.getAttributes().setStrength(roster.getSt());
		
		//player.getAttributes().setPlayer(player);
		
		//PlayerStatistics statistics = new PlayerStatistics();
		PlayerStatistics statistics = player.getStatistics();
		if(PlayerPositionType.GOALIE.equals(positionType)){
			
			statistics.setAssists(roster.getAssists());
			statistics.setPims(roster.getGoals());
			statistics.setGoalieGoalsAllowed(roster.getPims());
			statistics.setGoalieLosses(roster.getGwGoals());
			statistics.setGoalieMinutes(TransformUtils.getGoalieMinutes(roster.getPlusMinus(), roster.getPlusMinus2()));
			statistics.setGoalieShotsAgainst(TransformUtils.getGoalieShotsAgainst(roster.getShots(), roster.getShots2()));
			statistics.setGoalieShutOuts(roster.getPpGoals());
			statistics.setGoalieTies(roster.getGtGoals());
			statistics.setGoalieWins(roster.getShGoals());
			
			//calculated stats
			statistics.setGoalieGaa(TransformUtils.getGoalieGaa(
					statistics.getGoalieGoalsAllowed(), statistics.getGoalieMinutes()));
			statistics.setGoalieSavePct(TransformUtils.getGoalieSavePct(
					statistics.getGoalieGoalsAllowed(), statistics.getGoalieShotsAgainst()));

		}else{
			statistics.setAssists(roster.getAssists());
			statistics.setFarmAssists(roster.getFarmAssists());
			statistics.setFarmGamesPlayed(roster.getFarmGamesPlayed());
			statistics.setFarmGoals(roster.getFarmGoals());
			statistics.setFarmPim(TransformUtils.getPenaltyMinutes(roster.getFarmPim(), roster.getFarmPim2()));
			statistics.setGamesPlayed(roster.getGamesPlayed());
			statistics.setGoals(roster.getGoals());
			statistics.setGoalStreak(roster.getGoalStreak());
			statistics.setGtGoals(roster.getGtGoals());
			statistics.setGwGoals(roster.getGwGoals());
			statistics.setHits(TransformUtils.getHits(roster.getHits(), roster.getHits2()));
			statistics.setPims(TransformUtils.getPenaltyMinutes(roster.getPims(), roster.getPims2()));
			statistics.setPlusMinus(TransformUtils.getPlusMinus(roster.getPlusMinus(), roster.getPlusMinus2()));
			statistics.setPointStreak(roster.getPointStreak());
			statistics.setPpGoals(roster.getPpGoals());
			statistics.setShGoals(roster.getShGoals());
			statistics.setShots(TransformUtils.getShots(roster.getShots(), roster.getShots2()));	
			
			//calculated stats
			statistics.setShootingPct(TransformUtils.getShootingPct(
					statistics.getGoals(), statistics.getShots()));
			
		}
		//statistics.setPlayer(player);
		//player.setStatistics(statistics);
		
		return player;
	}
}
