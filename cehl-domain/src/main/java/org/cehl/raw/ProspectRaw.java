package org.cehl.raw;

public class ProspectRaw {
	private String name; 
	private String position; 
	private String hand; //26
	private int height; //27
	private int weight; //28
	private int age;
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
	private Integer salary;
	private int contractLength;
	private String birthPlace;
	private String teamName;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getHand() {
		return hand;
	}
	public void setHand(String hand) {
		this.hand = hand;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
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
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	@Override
	public String toString() {
		return String.format(
				"ProspectRaw [name=%s, position=%s, hand=%s, height=%s, weight=%s, age=%s, it=%s, sp=%s, st=%s, en=%s, du=%s, di=%s, sk=%s, pa=%s, pc=%s, df=%s, sc=%s, ex=%s, ld=%s, salary=%s, contractLength=%s, birthPlace=%s, teamName=%s]",
				name, position, hand, height, weight, age, it, sp, st, en, du, di, sk, pa, pc, df, sc, ex, ld, salary,
				contractLength, birthPlace, teamName);
	}
	
	
	
}
