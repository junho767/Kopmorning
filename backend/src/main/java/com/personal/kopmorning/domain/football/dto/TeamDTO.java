package com.personal.kopmorning.domain.football.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {
    private String team_key;
    private String team_name;
    private String team_country;
    private String team_founded;
    private String team_badge;
    private Coach coach;
    private List<PlayerDTO> players;

    public record Coach(
            String coach_name,
            String coach_country,
            String coach_age
    ) {}
}
