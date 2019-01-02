package org.cehl.model.cehl.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
//@Table(name = "player", uniqueConstraints = {@UniqueConstraint(columnNames = "playerName")})
@Table(name = "player")
public class Player implements Serializable{
	
    public Player() {
        super();
    }
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
	
	private int teamPlayerId;
	
	private String playerName;
	private String position;
	private int jerseyNumber = 0;
	private boolean rookieFlag = false;
	//private Integer hand;
	private String handType;
	private int height = 0;
	private int weight = 0;
	private int age = 0;
	private int injuryStatus = 0;
	private int condition = 0;
	private Integer salary;
	private String birthPlace;
	
	private boolean unassignedFlag = false;
	private boolean retiredFlag = false;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="player")
	@JsonManagedReference
    private PlayerAttributes attributes;

//	@OneToMany(cascade = CascadeType.ALL, mappedBy="player")
//    @OrderColumn(name = "stat_index")
//	private List<PlayerStatistics> statistics = new ArrayList<PlayerStatistics>();
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="player")
	@JsonManagedReference
	private PlayerStatistics statistics;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getTeamPlayerId() {
		return teamPlayerId;
	}

	public void setTeamPlayerId(int teamPlayerId) {
		this.teamPlayerId = teamPlayerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getJerseyNumber() {
		return jerseyNumber;
	}

	public void setJerseyNumber(int jerseyNumber) {
		this.jerseyNumber = jerseyNumber;
	}

	public boolean isRookieFlag() {
		return rookieFlag;
	}

	public void setRookieFlag(boolean rookieFlag) {
		this.rookieFlag = rookieFlag;
	}

	public String getHandType() {
		return handType;
	}

	public void setHandType(String handType) {
		this.handType = handType;
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

	public int getInjuryStatus() {
		return injuryStatus;
	}

	public void setInjuryStatus(int injuryStatus) {
		this.injuryStatus = injuryStatus;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public boolean isUnassignedFlag() {
		return unassignedFlag;
	}

	public void setUnassignedFlag(boolean unassignedFlag) {
		this.unassignedFlag = unassignedFlag;
	}

	public boolean isRetiredFlag() {
		return retiredFlag;
	}

	public void setRetiredFlag(boolean retiredFlag) {
		this.retiredFlag = retiredFlag;
	}

	public PlayerAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(PlayerAttributes attributes) {
		this.attributes = attributes;
	}

	public PlayerStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(PlayerStatistics statistics) {
		this.statistics = statistics;
	}

}
