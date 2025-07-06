package com.personal.kopmorning.domain.football.dto;

import lombok.Data;

@Data
public class StandingDTO {
    private String league_id;
    private String league_name;
    private String country_name;

    private String team_id;
    private String team_name;
    private String team_badge;

    private String overall_promotion;
    private String overall_league_position;
    private String overall_league_payed;
    private String overall_league_W;
    private String overall_league_D;
    private String overall_league_L;
    private String overall_league_GF;
    private String overall_league_GA;
    private String overall_league_PTS;

    private String home_league_position;
    private String home_promotion;
    private String home_league_payed;
    private String home_league_W;
    private String home_league_D;
    private String home_league_L;
    private String home_league_GF;
    private String home_league_GA;
    private String home_league_PTS;

    private String away_league_position;
    private String away_promotion;
    private String away_league_payed;
    private String away_league_W;
    private String away_league_D;
    private String away_league_L;
    private String away_league_GF;
    private String away_league_GA;
    private String away_league_PTS;

    private String league_round;
    private String fk_stage_key;
    private String stage_name;
}
