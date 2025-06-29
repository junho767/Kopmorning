package com.personal.kopmorning.domain.football.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TeamsResponse {
    private Competition competition;
    private List<TeamResponse> teams;

    public record Competition(
        Long id,
        String name,
        String code,
        String type,
        String emblem
    ) {}
}
