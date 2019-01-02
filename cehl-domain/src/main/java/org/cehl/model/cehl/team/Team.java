package org.cehl.model.cehl.team;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "team")
public class Team {
	public Team() {
        super();
    }
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
	
	private int teamId;
	private String teamName;
	private String teamAbbreviation;
	private String coachName;
	private int stadiumCapacity;
	private int ticketPrice;
	private int conferenceId;
	private int divisionId;
	private Integer teamFinances;
	private Integer coachSalary;
	private int coachStyle;
	private int goaliesLost;
	private int defenseLost;
	private int teamWins;
	private int teamLosses;
	private int teamTies;
	private int teamGoalsAllowed;
	private int teamGoalsFor;
	private int teamStreak;
	private boolean eliminated = false;
	
	
}
