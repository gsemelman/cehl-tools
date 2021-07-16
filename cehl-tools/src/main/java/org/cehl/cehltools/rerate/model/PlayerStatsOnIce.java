package org.cehl.cehltools.rerate.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "player_stats_on_ice")
public class PlayerStatsOnIce extends AbstractEntity {
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_season_id")
	private PlayerSeason playerSeason;

	private int year;
	private int gp;
	private int toi;
	
	private int cf;
	private int ca;
	private double cfPct;
	
	private int ff;
	private int fa;
	private double ffPct;
	
	private int oZoneStart;
	private int dZoneStart;
	private int nZoneStart;
	
	private int xgf;
	private int xga;
	private double xgfPct;
	
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
	public int getCf() {
		return cf;
	}
	public void setCf(int cf) {
		this.cf = cf;
	}
	public int getCa() {
		return ca;
	}
	public void setCa(int ca) {
		this.ca = ca;
	}
	public double getCfPct() {
		return cfPct;
	}
	public void setCfPct(double cfPct) {
		this.cfPct = cfPct;
	}
	public int getFf() {
		return ff;
	}
	public void setFf(int ff) {
		this.ff = ff;
	}
	public int getFa() {
		return fa;
	}
	public void setFa(int fa) {
		this.fa = fa;
	}
	public double getFfPct() {
		return ffPct;
	}
	public void setFfPct(double ffPct) {
		this.ffPct = ffPct;
	}
	public int getoZoneStart() {
		return oZoneStart;
	}
	public void setoZoneStart(int oZoneStart) {
		this.oZoneStart = oZoneStart;
	}
	public int getdZoneStart() {
		return dZoneStart;
	}
	public void setdZoneStart(int dZoneStart) {
		this.dZoneStart = dZoneStart;
	}
	public int getnZoneStart() {
		return nZoneStart;
	}
	public void setnZoneStart(int nZoneStart) {
		this.nZoneStart = nZoneStart;
	}
	public int getXgf() {
		return xgf;
	}
	public void setXgf(int xgf) {
		this.xgf = xgf;
	}
	public int getXga() {
		return xga;
	}
	public void setXga(int xga) {
		this.xga = xga;
	}
	public double getXgfPct() {
		return xgfPct;
	}
	public void setXgfPct(double xgfPct) {
		this.xgfPct = xgfPct;
	}
	

	
	
	
}
