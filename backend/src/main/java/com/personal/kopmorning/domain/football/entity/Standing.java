package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.StandingDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Standing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Standing(StandingDTO standingDTO) {
        this.league_id = standingDTO.getLeague_id();
        this.league_name = standingDTO.getLeague_name();
        this.country_name = standingDTO.getCountry_name();

        this.team_id = standingDTO.getTeam_id();
        this.team_name = standingDTO.getTeam_name();
        this.team_badge = standingDTO.getTeam_badge();

        this.overall_promotion = standingDTO.getOverall_promotion();
        this.overall_league_position = standingDTO.getOverall_league_position();
        this.overall_league_payed = standingDTO.getOverall_league_payed();
        this.overall_league_W = standingDTO.getOverall_league_W();
        this.overall_league_D = standingDTO.getOverall_league_D();
        this.overall_league_L = standingDTO.getOverall_league_L();
        this.overall_league_GF = standingDTO.getOverall_league_GF();
        this.overall_league_GA = standingDTO.getOverall_league_GA();
        this.overall_league_PTS = standingDTO.getOverall_league_PTS();

        this.home_league_position = standingDTO.getHome_league_position();
        this.home_promotion = standingDTO.getHome_promotion();
        this.home_league_payed = standingDTO.getHome_league_payed();
        this.home_league_W = standingDTO.getHome_league_W();
        this.home_league_D = standingDTO.getHome_league_D();
        this.home_league_L = standingDTO.getHome_league_L();
        this.home_league_GF = standingDTO.getHome_league_GF();
        this.home_league_GA = standingDTO.getHome_league_GA();
        this.home_league_PTS = standingDTO.getHome_league_PTS();

        this.away_league_position = standingDTO.getAway_league_position();
        this.away_promotion = standingDTO.getAway_promotion();
        this.away_league_payed = standingDTO.getAway_league_payed();
        this.away_league_W = standingDTO.getAway_league_W();
        this.away_league_D = standingDTO.getAway_league_D();
        this.away_league_L = standingDTO.getAway_league_L();
        this.away_league_GF = standingDTO.getAway_league_GF();
        this.away_league_GA = standingDTO.getAway_league_GA();
        this.away_league_PTS = standingDTO.getAway_league_PTS();

        this.league_round = standingDTO.getLeague_round();
        this.fk_stage_key = standingDTO.getFk_stage_key();
        this.stage_name = standingDTO.getStage_name();
    }
}
