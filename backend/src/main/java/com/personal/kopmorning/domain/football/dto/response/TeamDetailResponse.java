package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Team;
import lombok.Data;

import java.util.List;

@Data
public class TeamDetailResponse {
    private Long id;
    private Long founded;

    private String name;
    private String shortName;
    private String tla;

    private String crest;
    private String address;
    private String website;

    private String clubColors;
    private String venue;

    private List<PlayerResponse> players;

    public TeamDetailResponse(Team team, List<PlayerResponse> players) {
        this.id = team.getId();
        this.founded = team.getFounded();

        this.name = team.getName();
        this.shortName = team.getShortName();
        this.tla = team.getTla();

        this.crest = team.getCrest();
        this.address = team.getAddress();
        this.website = team.getWebsite();

        this.clubColors = team.getClubColors();
        this.venue = team.getVenue();

        this.players = players;
    }
}
