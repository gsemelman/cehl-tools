package org.cehl.cehltools.rerate.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "player")
public class Player extends AbstractEntity {
	

	private String name;
	private String position;
	private LocalDate dob;
	private String country;
	private int height;
	private int weight;
	
	@OneToMany(mappedBy = "player", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
	@OrderBy("year ASC")
	private Set<PlayerSeason> seasons = new HashSet<>();
	
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

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public Set<PlayerSeason> getSeasons() {
		return seasons;
	}

	public void setSeasons(Set<PlayerSeason> seasons) {
		this.seasons = seasons;
	}

	@Override
	public String toString() {
		return "Player [name=" + name + ", position=" + position + ", dob=" + dob + ", country=" + country + ", height="
				+ height + ", weight=" + weight + ", seasons=" + seasons + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(dob, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		return Objects.equals(dob, other.dob) && Objects.equals(name, other.name);
	}
	
	public PlayerSeason getSeasonByYear(int year) {
		return seasons.stream().filter(ps-> year == ps.getYear()).findFirst().orElse(null);
	}
	
	public int getAge() {
		return Period.between(getDob(), LocalDate.now()).getYears();
	}

}
