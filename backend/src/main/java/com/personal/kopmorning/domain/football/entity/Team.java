package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.response.TeamDTO;
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
    private String team_name;
    private String team_founded;
    private String team_country;
    private String team_badge;
    private String venue_name;

    public Team(TeamDTO teamDTO) {
        this.id = Long.valueOf(teamDTO.getTeam_key());
        this.team_name = teamDTO.getTeam_name();
        this.team_founded = teamDTO.getTeam_founded();
        this.team_country = teamDTO.getTeam_country();
        this.team_badge = teamDTO.getTeam_badge();
//        this.venue_name = teamDTO.getVenue().getFirst().venue_name();
    }
}
