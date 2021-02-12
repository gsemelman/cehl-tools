package org.cehl.cehltools.rerate.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public class PlayerRerateDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String team;
	private String name;
	private int age;
	private LocalDate dob;
	private String nationality;
	private Integer wieght;
	private Integer height;
	private char pos;
	private double it;
	private double sp;
	private double st;
	private double en;
	private double du;
	private double di;
	private double sk;
	private double pa;
	private double pc;
	private double df;
	private double sc;
	private double ex;
	private double ld;

	public PlayerRerateDto() {
		
	}
	
	public PlayerRerateDto(String name, int age, String nationality) {
		super();
		this.name = name;
		this.dob = LocalDate.now().minusYears(age);
		this.age = age;
		this.nationality = nationality;
	}

	public PlayerRerateDto(String name, LocalDate dob, String nationality) {
		super();
		this.name = name;
		this.dob = dob;
		this.age = Period.between(dob, LocalDate.now()).getYears();
		this.nationality = nationality;
	}
	
	public PlayerRerateDto(String name, LocalDate dob, String nationality, String team) {
		super();
		this.name = name;
		this.dob = dob;
		this.age = Period.between(dob, LocalDate.now()).getYears();
		this.nationality = nationality;
		this.team = team;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlayerRerateDto [team=");
		builder.append(team);
		builder.append(", name=");
		builder.append(name);
		builder.append(", age=");
		builder.append(age);
		builder.append(", dob=");
		builder.append(dob);
		builder.append("]");
		return builder.toString();
	}
	

	public char getPos() {
		return pos;
	}

	public void setPos(char pos) {
		this.pos = pos;
	}

	public double getIt() {
		return it;
	}

	public void setIt(double it) {
		this.it = it;
	}

	public double getSp() {
		return sp;
	}

	public void setSp(double sp) {
		this.sp = sp;
	}

	public double getSt() {
		return st;
	}

	public void setSt(double st) {
		this.st = st;
	}

	public double getEn() {
		return en;
	}

	public void setEn(double en) {
		this.en = en;
	}

	public double getDu() {
		return du;
	}

	public void setDu(double du) {
		this.du = du;
	}

	public double getDi() {
		return di;
	}

	public void setDi(double di) {
		this.di = di;
	}

	public double getSk() {
		return sk;
	}

	public void setSk(double sk) {
		this.sk = sk;
	}

	public double getPa() {
		return pa;
	}

	public void setPa(double pa) {
		this.pa = pa;
	}

	public double getPc() {
		return pc;
	}

	public void setPc(double pc) {
		this.pc = pc;
	}

	public double getDf() {
		return df;
	}

	public void setDf(double df) {
		this.df = df;
	}

	public double getSc() {
		return sc;
	}

	public void setSc(double sc) {
		this.sc = sc;
	}

	public double getEx() {
		return ex;
	}

	public void setEx(double ex) {
		this.ex = ex;
	}

	public double getLd() {
		return ld;
	}

	public void setLd(double ld) {
		this.ld = ld;
	}

	public Integer getWieght() {
		return wieght;
	}

	public void setWieght(Integer wieght) {
		this.wieght = wieght;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}


	
	
}
