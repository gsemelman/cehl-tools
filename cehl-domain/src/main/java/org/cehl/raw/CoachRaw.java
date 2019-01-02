package org.cehl.raw;

import java.io.Serializable;

public class CoachRaw implements Serializable {

	public CoachRaw() {
		super();
	}

	private int coachId;
	private String name; 
	private int of;
	private int df;
	private int ex;
	private int ld;
	private Integer salary;
	private Integer teamId;
	
	public int getCoachId() {
		return coachId;
	}
	public void setCoachId(int coachId) {
		this.coachId = coachId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOf() {
		return of;
	}
	public void setOf(int of) {
		this.of = of;
	}
	public int getDf() {
		return df;
	}
	public void setDf(int df) {
		this.df = df;
	}
	public int getEx() {
		return ex;
	}
	public void setEx(int ex) {
		this.ex = ex;
	}
	public int getLd() {
		return ld;
	}
	public void setLd(int ld) {
		this.ld = ld;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public Integer getTeamId() {
		return teamId;
	}
	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}
	@Override
	public String toString() {
		return String.format("CoachRaw [coachId=%s, name=%s, of=%s, df=%s, ex=%s, ld=%s, salary=%s, teamId=%s]",
				coachId, name, of, df, ex, ld, salary, teamId);
	}

	
}
