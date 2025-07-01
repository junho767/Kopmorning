package com.personal.kopmorning.domain.football.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.personal.kopmorning.domain.football.entity.Player;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerResponse {
    private Long id;
    private Long marketValue;
    private String name;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nationality;
    private String position;
    private String shirtNumber;

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.dateOfBirth = player.getDateOfBirth();
        this.nationality = player.getNationality();
    }

}
