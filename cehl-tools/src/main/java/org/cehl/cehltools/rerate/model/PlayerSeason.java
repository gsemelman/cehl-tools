package org.cehl.cehltools.rerate.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "player_season")
public class PlayerSeason extends AbstractEntity {

	private int year;
	private String team;
	private String position;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
	
    @OneToOne(mappedBy = "playerSeason", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, optional = true)
	private PlayerStatsAllStrengths statsAll = new PlayerStatsAllStrengths();
    
    @OneToOne(mappedBy = "playerSeason", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, optional = true)
	private PlayerStatsPp statsPp = new PlayerStatsPp();
    
    @OneToOne(mappedBy = "playerSeason", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, optional = true)
	private PlayerStatsPk statsPk = new PlayerStatsPk();
    
    @OneToOne(mappedBy = "playerSeason", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, optional = true)
	private PlayerStatsOnIce statsOnIce = new PlayerStatsOnIce();
	
	public PlayerSeason() {
		
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public PlayerStatsAllStrengths getStatsAll() {
		return statsAll;
	}

	public void setStatsAll(PlayerStatsAllStrengths statsAll) {
		this.statsAll = statsAll;
	}

	public PlayerStatsPp getStatsPp() {
		return statsPp;
	}

	public void setStatsPp(PlayerStatsPp statsPp) {
		this.statsPp = statsPp;
	}

	public PlayerStatsPk getStatsPk() {
		return statsPk;
	}

	public void setStatsPk(PlayerStatsPk statsPk) {
		this.statsPk = statsPk;
	}

	public PlayerStatsOnIce getStatsOnIce() {
		return statsOnIce;
	}

	public void setStatsOnIce(PlayerStatsOnIce statsOnIce) {
		this.statsOnIce = statsOnIce;
	}

	@Override
	public String toString() {
		return "PlayerSeason [year=" + year + ", team=" + team + ", position=" + position + ", statsAll=" + statsAll
				+ ", statsPp=" + statsPp + ", statsPk=" + statsPk + ", statsOnIce=" + statsOnIce + "]";
	}



}
