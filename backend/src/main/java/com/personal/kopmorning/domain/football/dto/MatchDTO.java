package com.personal.kopmorning.domain.football.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDTO {
    private List<Match> matches;

    public record Match(
            Long id,
            String utcDate,
            String status,
            Competition competition,
            Long matchday,
            Team homeTeam,
            Team awayTeam,
            Score score
    ) {}

    public record Competition(
            Long id,
            String name,
            String code,
            String type,
            String emblem
    ) {}

    public record Team(
            Long id,
            String name,
            String shortName,
            String tla,
            String crest
    ) {}

    public record Score(
            String winner,
            FullTime fullTime,
            HalfTime halfTime
    ) {}

    public record FullTime(
            Long home,
            Long away
    ) {}

    public record HalfTime(
            Long home,
            Long away
    ) {}
}
