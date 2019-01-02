package org.cehl.raw.transformer.coach;

import java.io.Serializable;

public class CoachImport implements Serializable {

	public CoachImport() {
		super();
	}

	private String name; 
	private int of;
	private int df;
	private int ex;
	private int ld;
	private Integer salary;
	private String teamAbbr;
	
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
	public String getTeamAbbr() {
		return teamAbbr;
	}
	public void setTeamAbbr(String teamAbbr) {
		this.teamAbbr = teamAbbr;
	}
	@Override
	public String toString() {
		return String.format("CoachImport [name=%s, of=%s, df=%s, ex=%s, ld=%s, salary=%s, teamAbbr=%s]", name, of, df,
				ex, ld, salary, teamAbbr);
	}

}
