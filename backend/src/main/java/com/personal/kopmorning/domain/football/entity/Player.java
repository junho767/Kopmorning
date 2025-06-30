package com.personal.kopmorning.domain.football.entity;


import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    public Long id;
    public Long teamId;
    private Long marketValue;
    public String name;
    public String firstName;
    public String lastName;
    public String dateOfBirth;
    public String nationality;
    public String position;
    public String shirtNumber;

    public Player(PlayerResponse response) {
        this.id = response.getId();
        this.teamId = response.getTeamId();
        this.marketValue = response.getMarketValue();
        this.name = response.getName();
        this.firstName = response.getFirstName();
        this.lastName = response.getLastName();
        this.dateOfBirth = response.getDateOfBirth();
        this.nationality = response.getNationality();
        this.position = response.getPosition();
        this.shirtNumber = response.getShirtNumber();
    }
}
