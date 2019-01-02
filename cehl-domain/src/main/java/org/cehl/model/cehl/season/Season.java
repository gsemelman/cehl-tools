package org.cehl.model.cehl.season;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "season", uniqueConstraints = {@UniqueConstraint(columnNames = "seasonNumber"), @UniqueConstraint(columnNames = "playoffFlag")})
public class Season {
	
    public Season() {
        super();
    }
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
	
	private int seasonNumber;
	private boolean playoffFlag = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getSeasonNumber() {
		return seasonNumber;
	}
	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = seasonNumber;
	}
	public boolean isPlayoffFlag() {
		return playoffFlag;
	}
	public void setPlayoffFlag(boolean playoffFlag) {
		this.playoffFlag = playoffFlag;
	}
	
}
