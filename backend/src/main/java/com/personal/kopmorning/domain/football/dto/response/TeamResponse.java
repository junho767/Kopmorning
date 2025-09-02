package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Team;
import lombok.Data;

@Data
public class TeamResponse {
    private Long id;
    private String shortName;
    private String tla;
    private String crest;

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.shortName = team.getShortName();
        this.tla = team.getTla();
        this.crest = team.getCrest();
    }
}
