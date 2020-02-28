package org.cehl.cehltools.rerate.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "player_stats_pk")
public class PlayerStatsPk extends AbstractEntity {
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_season_id")
	private PlayerSeason playerSeason;

	private int year;
	private int gp;
	private int toi;
	private int goals;
	private int assists;
	private int assistsFirst;
	private int assistsSecond;
	private int points;
	private int shots;
	private double shPct;
	private int rushAttempt;
	private int pim;
	private int penMinor;
	private int penMajor;
	private int penMisconduct;
	private int penDrawn;
	private int giveAway;
	private int takeAway;
	private int hits;
	private int shotsBlocked;
	public PlayerSeason getPlayerSeason() {
		return playerSeason;
	}
	public void setPlayerSeason(PlayerSeason playerSeason) {
		this.playerSeason = playerSeason;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getGp() {
		return gp;
	}
	public void setGp(int gp) {
		this.gp = gp;
	}
	public int getToi() {
		return toi;
	}
	public void setToi(int toi) {
		this.toi = toi;
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
	public int getAssistsFirst() {
		return assistsFirst;
	}
	public void setAssistsFirst(int assistsFirst) {
		this.assistsFirst = assistsFirst;
	}
	public int getAssistsSecond() {
		return assistsSecond;
	}
	public void setAssistsSecond(int assistsSecond) {
		this.assistsSecond = assistsSecond;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getShots() {
		return shots;
	}
	public void setShots(int shots) {
		this.shots = shots;
	}

	public double getShPct() {
		return shPct;
	}
	public void setShPct(double shPct) {
		this.shPct = shPct;
	}
	public int getRushAttempt() {
		return rushAttempt;
	}
	public void setRushAttempt(int rushAttempt) {
		this.rushAttempt = rushAttempt;
	}
	public int getPim() {
		return pim;
	}
	public void setPim(int pim) {
		this.pim = pim;
	}
	public int getPenMinor() {
		return penMinor;
	}
	public void setPenMinor(int penMinor) {
		this.penMinor = penMinor;
	}
	public int getPenMajor() {
		return penMajor;
	}
	public void setPenMajor(int penMajor) {
		this.penMajor = penMajor;
	}
	public int getPenMisconduct() {
		return penMisconduct;
	}
	public void setPenMisconduct(int penMisconduct) {
		this.penMisconduct = penMisconduct;
	}
	public int getPenDrawn() {
		return penDrawn;
	}
	public void setPenDrawn(int penDrawn) {
		this.penDrawn = penDrawn;
	}
	public int getGiveAway() {
		return giveAway;
	}
	public void setGiveAway(int giveAway) {
		this.giveAway = giveAway;
	}
	public int getTakeAway() {
		return takeAway;
	}
	public void setTakeAway(int takeAway) {
		this.takeAway = takeAway;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public int getShotsBlocked() {
		return shotsBlocked;
	}
	public void setShotsBlocked(int shotsBlocked) {
		this.shotsBlocked = shotsBlocked;
	}
	@Override
	public String toString() {
		return "PlayerStatPk [year=" + year + ", gp=" + gp + ", toi="
				+ toi + ", goals=" + goals + ", assists=" + assists + ", assistsFirst=" + assistsFirst
				+ ", assistsSecond=" + assistsSecond + ", points=" + points + ", shots=" + shots + ", shPct=" + shPct
				+ ", rushAttempt=" + rushAttempt + ", pim=" + pim + ", penMinor=" + penMinor + ", penMajor=" + penMajor
				+ ", penMisconduct=" + penMisconduct + ", penDrawn=" + penDrawn + ", giveAway=" + giveAway
				+ ", takeAway=" + takeAway + ", hits=" + hits + ", shotsBlocked=" + shotsBlocked + "]";
	}

	
	
	
}
