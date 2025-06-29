package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    private Long id;
    private String name;
    private String shortName;
    private String tla;
    private String website;
    private String founded;
    private String clubColors;
    private String venue;

    public Team(TeamResponse teamResponse) {
        this.id = teamResponse.getId();
        this.name = teamResponse.getName();
        this.shortName = teamResponse.getShortName();
        this.tla = teamResponse.getTla();
        this.website = teamResponse.getWebsite();
        this.founded = teamResponse.getFounded();
        this.clubColors = teamResponse.getClubColors();
        this.venue = teamResponse.getVenue();
    }
}
