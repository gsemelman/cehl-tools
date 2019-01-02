package org.cehl.raw;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

public class DrsRaw implements Serializable{
    
    public DrsRaw() {
        super();
    }
	
    private int proFarmStatus1; //24
	private int proFarmStatus2; //25
	private String name; 
	private int position; 
	private int jersey; 
	private int vetRookieStatus1; 
	private int vetRookieStatus2; 
	private int hand; //26
	private int height; //27
	private int weight; //28
	private int age;
	private int injStatus;
	private int condition;
	private int it;
	private int sp;
	private int st;
	private int en;
	private int du;
	private int di;
	private int sk;
	private int pa;
	private int pc;
	private int df;
	private int sc;
	private int ex;
	private int ld;
	private int filler1;
	private Integer salary;
	private int contractLength;
	private int suspStatus;
	private int gamesPlayed;
	private int goals;
	private int assists;
	private int plusMinus;
	private int plusMinus2;
	private int pims;
	private int pims2;
	private int shots;
	private int shots2;
	private int ppGoals;
	private int shGoals;
	private int gwGoals;
	private int gtGoals;
	private int goalStreak;
	private int pointStreak;
	private int filler3;
	private int filler4;
	private int filler5;
	private int filler6;
	private int farmGoals;
	private int farmAssists;
	private int filler7;
	private int filler8;
	private int filler9;
	private String birthPlace;
	private int filler10;
	private int filler11;
	private int farmGamesPlayed;
	private int farmPim;
	private int farmPim2;
	private int hits;
	private int hits2;
	

	public int getProFarmStatus1() {
		return proFarmStatus1;
	}
	public void setProFarmStatus1(int proFarmStatus1) {
		this.proFarmStatus1 = proFarmStatus1;
	}
	public int getProFarmStatus2() {
		return proFarmStatus2;
	}
	public void setProFarmStatus2(int proFarmStatus2) {
		this.proFarmStatus2 = proFarmStatus2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getJersey() {
		return jersey;
	}
	public void setJersey(int jersey) {
		this.jersey = jersey;
	}
	public int getVetRookieStatus1() {
		return vetRookieStatus1;
	}
	public void setVetRookieStatus1(int vetRookieStatus1) {
		this.vetRookieStatus1 = vetRookieStatus1;
	}
	public int getVetRookieStatus2() {
		return vetRookieStatus2;
	}
	public void setVetRookieStatus2(int vetRookieStatus2) {
		this.vetRookieStatus2 = vetRookieStatus2;
	}
	public int getHand() {
		return hand;
	}
	public void setHand(int hand) {
		this.hand = hand;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getInjStatus() {
		return injStatus;
	}
	public void setInjStatus(int injStatus) {
		this.injStatus = injStatus;
	}
	public int getCondition() {
		return condition;
	}
	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getIt() {
		return it;
	}
	public void setIt(int it) {
		this.it = it;
	}
	public int getSp() {
		return sp;
	}
	public void setSp(int sp) {
		this.sp = sp;
	}
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	public int getEn() {
		return en;
	}
	public void setEn(int en) {
		this.en = en;
	}
	public int getDu() {
		return du;
	}
	public void setDu(int du) {
		this.du = du;
	}
	public int getDi() {
		return di;
	}
	public void setDi(int di) {
		this.di = di;
	}
	public int getSk() {
		return sk;
	}
	public void setSk(int sk) {
		this.sk = sk;
	}
	public int getPa() {
		return pa;
	}
	public void setPa(int pa) {
		this.pa = pa;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getDf() {
		return df;
	}
	public void setDf(int df) {
		this.df = df;
	}
	public int getSc() {
		return sc;
	}
	public void setSc(int sc) {
		this.sc = sc;
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
	public int getFiller1() {
		return filler1;
	}
	public void setFiller1(int filler1) {
		this.filler1 = filler1;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public int getContractLength() {
		return contractLength;
	}
	public void setContractLength(int contractLength) {
		this.contractLength = contractLength;
	}
	public int getSuspStatus() {
		return suspStatus;
	}
	public void setSuspStatus(int suspStatus) {
		this.suspStatus = suspStatus;
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
	public int getPlusMinus2() {
		return plusMinus2;
	}
	public void setPlusMinus2(int plusMinus2) {
		this.plusMinus2 = plusMinus2;
	}
	public int getPims() {
		return pims;
	}
	public void setPims(int pims) {
		this.pims = pims;
	}
	public int getPims2() {
		return pims2;
	}
	public void setPims2(int pims2) {
		this.pims2 = pims2;
	}
	public int getShots() {
		return shots;
	}
	public void setShots(int shots) {
		this.shots = shots;
	}
	public int getShots2() {
		return shots2;
	}
	public void setShots2(int shots2) {
		this.shots2 = shots2;
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
	public int getFiller3() {
		return filler3;
	}
	public void setFiller3(int filler3) {
		this.filler3 = filler3;
	}
	public int getFiller4() {
		return filler4;
	}
	public void setFiller4(int filler4) {
		this.filler4 = filler4;
	}
	public int getFiller5() {
		return filler5;
	}
	public void setFiller5(int filler5) {
		this.filler5 = filler5;
	}
	public int getFiller6() {
		return filler6;
	}
	public void setFiller6(int filler6) {
		this.filler6 = filler6;
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
	public int getFiller7() {
		return filler7;
	}
	public void setFiller7(int filler7) {
		this.filler7 = filler7;
	}
	public int getFiller8() {
		return filler8;
	}
	public void setFiller8(int filler8) {
		this.filler8 = filler8;
	}
	public int getFiller9() {
		return filler9;
	}
	public void setFiller9(int filler9) {
		this.filler9 = filler9;
	}
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	public int getFiller10() {
		return filler10;
	}
	public void setFiller10(int filler10) {
		this.filler10 = filler10;
	}
	public int getFiller11() {
		return filler11;
	}
	public void setFiller11(int filler11) {
		this.filler11 = filler11;
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
	public int getFarmPim2() {
		return farmPim2;
	}
	public void setFarmPim2(int farmPim2) {
		this.farmPim2 = farmPim2;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public int getHits2() {
		return hits2;
	}
	public void setHits2(int hits2) {
		this.hits2 = hits2;
	}
	@Override
	public String toString() {
		return String
				.format("DrsRaw [proFarmStatus1=%s, proFarmStatus2=%s, name=%s, position=%s, jersey=%s, vetRookieStatus1=%s, vetRookieStatus2=%s, hand=%s, height=%s, weight=%s, age=%s, injStatus=%s, condition=%s, it=%s, sp=%s, st=%s, en=%s, du=%s, di=%s, sk=%s, pa=%s, pc=%s, df=%s, sc=%s, ex=%s, ld=%s, filler1=%s, salary=%s, contractLength=%s, suspStatus=%s, gamesPlayed=%s, goals=%s, assists=%s, plusMinus=%s, plusMinus2=%s, pims=%s, pims2=%s, shots=%s, shots2=%s, ppGoals=%s, shGoals=%s, gwGoals=%s, gtGoals=%s, goalStreak=%s, pointStreak=%s, filler3=%s, filler4=%s, filler5=%s, filler6=%s, farmGoals=%s, farmAssists=%s, filler7=%s, filler8=%s, filler9=%s, birthPlace=%s, filler10=%s, filler11=%s, farmGamesPlayed=%s, farmPim=%s, farmPim2=%s, hits=%s, hits2=%s]",
						proFarmStatus1, proFarmStatus2, name, position, jersey,
						vetRookieStatus1, vetRookieStatus2, hand, height,
						weight, age, injStatus, condition, it, sp, st, en, du,
						di, sk, pa, pc, df, sc, ex, ld, filler1, salary,
						contractLength, suspStatus, gamesPlayed, goals,
						assists, plusMinus, plusMinus2, pims, pims2, shots,
						shots2, ppGoals, shGoals, gwGoals, gtGoals, goalStreak,
						pointStreak, filler3, filler4, filler5, filler6,
						farmGoals, farmAssists, filler7, filler8, filler9,
						birthPlace, filler10, filler11, farmGamesPlayed,
						farmPim, farmPim2, hits, hits2);
	}

	
	
}

