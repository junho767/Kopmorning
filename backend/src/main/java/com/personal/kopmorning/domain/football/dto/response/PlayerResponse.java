package com.personal.kopmorning.domain.football.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerResponse {
    private Long id;
    private Long teamId;
    private Long marketValue;
    private String name;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nationality;
    private String position;
    private String shirtNumber;

}
