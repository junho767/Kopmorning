package com.personal.kopmorning.domain.football.dto;

import lombok.Data;

import java.util.List;

@Data
public class StandingDTO {
    private List<Standing> standings;

    public record Standing(
            List<Table> table
    ) {}

    public record Table(
            Team team,
            Long position,
            Long playedGames,
            Long won,
            Long draw,
            Long lost,
            Long points,
            Long goalsFor,
            Long goalsAgainst,
            Long goalDifference
    ) {}

    public record Team(
            Long id
    ) {}
}
