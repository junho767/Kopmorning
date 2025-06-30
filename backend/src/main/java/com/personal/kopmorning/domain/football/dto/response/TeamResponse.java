package com.personal.kopmorning.domain.football.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamResponse {
    private Long id;
    private String name;
    private String shortName;
    private String tla;
    private String website;
    private String founded;
    private String clubColors;
    private String venue;
    private Coach coach;
    private List<PlayerResponse> squad;

    public record Coach(
            Long id,
            String name,
            String dateOfBirth,
            String nationality
    ) {}
}
