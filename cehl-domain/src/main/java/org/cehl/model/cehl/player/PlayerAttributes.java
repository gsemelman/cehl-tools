package org.cehl.model.cehl.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="player_attributes")
public class PlayerAttributes {

	@Id
	@Column(name="id", unique=true, nullable=false)
	@GeneratedValue(generator="gen")
	@GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="player")})
	private Long id;
	
	@OneToOne
    @PrimaryKeyJoinColumn
    @JsonBackReference
	private Player player;
	
	private int intensity;
	private int speed;
	private int strength;
	private int endurence;
	private int duribility;
	private int discipline;
	private int skating;
	@Column(name="pass_accuracy")
	private int passAccuracy;
	@Column(name="puck_control")
	private int puckControl;
	private int defense;
	private int scoring;
	private int experience;
	private int leadership;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public int getIntensity() {
		return intensity;
	}
	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public int getEndurence() {
		return endurence;
	}
	public void setEndurence(int endurence) {
		this.endurence = endurence;
	}
	public int getDuribility() {
		return duribility;
	}
	public void setDuribility(int duribility) {
		this.duribility = duribility;
	}
	public int getDiscipline() {
		return discipline;
	}
	public void setDiscipline(int discipline) {
		this.discipline = discipline;
	}
	public int getSkating() {
		return skating;
	}
	public void setSkating(int skating) {
		this.skating = skating;
	}
	public int getPassAccuracy() {
		return passAccuracy;
	}
	public void setPassAccuracy(int passAccuracy) {
		this.passAccuracy = passAccuracy;
	}
	public int getPuckControl() {
		return puckControl;
	}
	public void setPuckControl(int puckControl) {
		this.puckControl = puckControl;
	}
	public int getDefense() {
		return defense;
	}
	public void setDefense(int defense) {
		this.defense = defense;
	}
	public int getScoring() {
		return scoring;
	}
	public void setScoring(int scoring) {
		this.scoring = scoring;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public int getLeadership() {
		return leadership;
	}
	public void setLeadership(int leadership) {
		this.leadership = leadership;
	}

}
