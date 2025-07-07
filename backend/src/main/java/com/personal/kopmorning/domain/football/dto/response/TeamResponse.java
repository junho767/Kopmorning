package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Team;
import lombok.Data;

@Data
public class TeamResponse {
    private Long id;
    private String name;
    private String shortName;
    private String tla;
    private Long founded;
    private String address;
    private String website;
    private String clubColors;
    private String venue;
    private String crest;

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.shortName = team.getShortName();
        this.tla = team.getTla();
        this.founded = team.getFounded();
        this.address = team.getAddress();
        this.website = team.getWebsite();
        this.clubColors = team.getClubColors();
        this.venue = team.getVenue();
        this.crest = team.getCrest();
    }
}
