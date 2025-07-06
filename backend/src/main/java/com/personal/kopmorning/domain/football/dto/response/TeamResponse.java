package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Team;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {
    private Long team_key;
    private String team_name;
    private String team_country;
    private String team_founded;
    private String team_badge;

    public TeamResponse(Team team) {
        this.team_key = team.getId();
        this.team_name = team.getTeam_name();
        this.team_country = team.getTeam_country();
        this.team_founded = team.getTeam_founded();
        this.team_badge = team.getTeam_badge();
    }
}
