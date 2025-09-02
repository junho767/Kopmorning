package com.personal.kopmorning.domain.football.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {
    private List<Team> teams;

    public record Team(
            Long id,
            String name,
            String shortName,
            String tla,
            String crest,
            String address,
            String website,
            Long founded,
            String clubColors,
            String venue,
            Coach coach,
            List<Player> squad
    ) {
    }

    public record Coach(
            Long id,
            String name,
            String dateOfBirth,
            String nationality,
            Contract contract
    ) {
    }

    public record Contract(
            String start,
            String until
    ) {}

    public record Player(
            Long id,
            String name,
            String position,
            String dateOfBirth,
            String nationality
    ) {}
}
