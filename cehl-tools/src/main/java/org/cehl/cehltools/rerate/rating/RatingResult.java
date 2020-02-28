package org.cehl.cehltools.rerate.rating;

import org.cehl.cehltools.rerate.model.PlayerSeason;

public class RatingResult {
	
	private PlayerSeason season;
	private int year;
	private int gp;
	private int it;
	private int st;
	private int di;
	private int pa;
	private int sc;

	public RatingResult(PlayerSeason season, int gp, int it, int st, int di, int pa, int sc) {
		super();
		this.season= season;
		this.gp = gp;
		this.it = it;
		this.st = st;
		this.di = di;
		this.pa = pa;
		this.sc = sc;
	}
	public PlayerSeason getSeason() {
		return season;
	}
	public void setSeason(PlayerSeason season) {
		this.season = season;
	}

	public int getGp() {
		return gp;
	}
	public void setGp(int gp) {
		this.gp = gp;
	}
	public int getIt() {
		return it;
	}
	public void setIt(int it) {
		this.it = it;
	}
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	public int getDi() {
		return di;
	}
	public void setDi(int di) {
		this.di = di;
	}
	public int getPa() {
		return pa;
	}
	public void setPa(int pa) {
		this.pa = pa;
	}
	public int getSc() {
		return sc;
	}
	public void setSc(int sc) {
		this.sc = sc;
	}

	
	
	
}

