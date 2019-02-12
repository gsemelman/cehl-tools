package org.cehl.cehltools.dto;

public class ContractDto {
	private String name; //0-21
	private String teamName;
	private Integer salary;
	private int contractLength;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
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
	@Override
	public String toString() {
		return "ContractDto [name=" + name + ", teamName=" + teamName + ", salary=" + salary + ", contractLength="
				+ contractLength + "]";
	}
	
	
}
