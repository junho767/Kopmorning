package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.TeamDTO;
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
    private Long founded;
    private String address;
    private String website;
    private String clubColors;
    private String venue;
    private String crest;

    public Team(TeamDTO.Team teamDTO) {
        this.id = teamDTO.id();
        this.name = teamDTO.name();
        this.shortName = teamDTO.shortName();
        this.tla = teamDTO.tla();
        this.founded = teamDTO.founded();
        this.address = teamDTO.address();
        this.website = teamDTO.website();
        this.clubColors = teamDTO.clubColors();
        this.venue = teamDTO.venue();
        this.crest = teamDTO.crest();
    }
}
