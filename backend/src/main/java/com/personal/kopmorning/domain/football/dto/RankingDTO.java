package com.personal.kopmorning.domain.football.dto;

import lombok.Data;

import java.util.List;

@Data
public class RankingDTO {
    private List<Scorer> scorers;

    public record Scorer(
            Long goals,
            Long assists,
            Long penalties,
            Team team,
            Player player
    ) {}

    public record Team(
            Long id,
            String name
    ) {}

    public record Player(
            Long id,
            String name
    ) {}
}
