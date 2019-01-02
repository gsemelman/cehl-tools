package org.cehl.model.cehl.player;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="player_statistics")
public class PlayerStatistics {

//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(name="id", unique=true, nullable=false)
//	private int id;
	
	@Id
	@Column(name="id", unique=true, nullable=false)
	@GeneratedValue(generator="gen")
	@GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="player")})
	private Long id;
	
	@OneToOne
    @PrimaryKeyJoinColumn
    @JsonBackReference
	private Player player;
	
	private int gamesPlayed = 0;
	private int goals = 0;
	private int assists = 0;
	private int plusMinus = 0;
	private int pims = 0;
	private int shots = 0;
	private int ppGoals = 0;
	private int shGoals = 0;
	private int gwGoals = 0;
	private int gtGoals = 0;
	private int goalStreak = 0;
	private int pointStreak = 0;
	private int farmGoals = 0;
	private int farmAssists = 0;
	private int farmGamesPlayed = 0;
	private int farmPim = 0;
	private int hits = 0;
	private BigDecimal shootingPct = new BigDecimal(0);
	
	//goalie stats
	private int goalieGoalsAllowed = 0;
	private int goalieShutOuts = 0;
	private int goalieWins = 0;
	private int goalieLosses = 0;
	private int goalieTies = 0;
	private int goalieShotsAgainst = 0;
	private int goalieMinutes = 0;
	private BigDecimal goalieGaa = new BigDecimal(0);
	private BigDecimal goalieSavePct = new BigDecimal(0);

//	@ManyToOne
//	@JoinColumn(name="player_id")
//	private Player player;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public int getPlusMinus() {
		return plusMinus;
	}

	public void setPlusMinus(int plusMinus) {
		this.plusMinus = plusMinus;
	}

	public int getPims() {
		return pims;
	}

	public void setPims(int pims) {
		this.pims = pims;
	}

	public int getShots() {
		return shots;
	}

	public void setShots(int shots) {
		this.shots = shots;
	}

	public int getPpGoals() {
		return ppGoals;
	}

	public void setPpGoals(int ppGoals) {
		this.ppGoals = ppGoals;
	}

	public int getShGoals() {
		return shGoals;
	}

	public void setShGoals(int shGoals) {
		this.shGoals = shGoals;
	}

	public int getGwGoals() {
		return gwGoals;
	}

	public void setGwGoals(int gwGoals) {
		this.gwGoals = gwGoals;
	}

	public int getGtGoals() {
		return gtGoals;
	}

	public void setGtGoals(int gtGoals) {
		this.gtGoals = gtGoals;
	}

	public int getGoalStreak() {
		return goalStreak;
	}

	public void setGoalStreak(int goalStreak) {
		this.goalStreak = goalStreak;
	}

	public int getPointStreak() {
		return pointStreak;
	}

	public void setPointStreak(int pointStreak) {
		this.pointStreak = pointStreak;
	}

	public int getFarmGoals() {
		return farmGoals;
	}

	public void setFarmGoals(int farmGoals) {
		this.farmGoals = farmGoals;
	}

	public int getFarmAssists() {
		return farmAssists;
	}

	public void setFarmAssists(int farmAssists) {
		this.farmAssists = farmAssists;
	}

	public int getFarmGamesPlayed() {
		return farmGamesPlayed;
	}

	public void setFarmGamesPlayed(int farmGamesPlayed) {
		this.farmGamesPlayed = farmGamesPlayed;
	}

	public int getFarmPim() {
		return farmPim;
	}

	public void setFarmPim(int farmPim) {
		this.farmPim = farmPim;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public BigDecimal getShootingPct() {
		return shootingPct;
	}

	public void setShootingPct(BigDecimal shootingPct) {
		this.shootingPct = shootingPct;
	}

	public int getGoalieGoalsAllowed() {
		return goalieGoalsAllowed;
	}

	public void setGoalieGoalsAllowed(int goalieGoalsAllowed) {
		this.goalieGoalsAllowed = goalieGoalsAllowed;
	}

	public int getGoalieShutOuts() {
		return goalieShutOuts;
	}

	public void setGoalieShutOuts(int goalieShutOuts) {
		this.goalieShutOuts = goalieShutOuts;
	}

	public int getGoalieWins() {
		return goalieWins;
	}

	public void setGoalieWins(int goalieWins) {
		this.goalieWins = goalieWins;
	}

	public int getGoalieLosses() {
		return goalieLosses;
	}

	public void setGoalieLosses(int goalieLosses) {
		this.goalieLosses = goalieLosses;
	}

	public int getGoalieTies() {
		return goalieTies;
	}

	public void setGoalieTies(int goalieTies) {
		this.goalieTies = goalieTies;
	}

	public int getGoalieShotsAgainst() {
		return goalieShotsAgainst;
	}

	public void setGoalieShotsAgainst(int goalieShotsAgainst) {
		this.goalieShotsAgainst = goalieShotsAgainst;
	}

	public int getGoalieMinutes() {
		return goalieMinutes;
	}

	public void setGoalieMinutes(int goalieMinutes) {
		this.goalieMinutes = goalieMinutes;
	}

	public BigDecimal getGoalieGaa() {
		return goalieGaa;
	}

	public void setGoalieGaa(BigDecimal goalieGaa) {
		this.goalieGaa = goalieGaa;
	}

	public BigDecimal getGoalieSavePct() {
		return goalieSavePct;
	}

	public void setGoalieSavePct(BigDecimal goalieSavePct) {
		this.goalieSavePct = goalieSavePct;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
