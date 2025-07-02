package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.response.PlayerDTO;
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
public class PlayerStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long playerId;
    private String matchPlayed;
    private String goals;
    private String yellowCards;
    private String redCards;
    private String injured;
    private String substituteOut;
    private String substitutesOnBench;
    private String assists;
    private String isCaptain;
    private String shotsTotal;
    private String goalsConceded;
    private String foulsCommitted;
    private String tackles;
    private String blocks;
    private String crossesTotal;
    private String interceptions;
    private String clearances;
    private String dispossesed;
    private String saves;
    private String insideBoxSaves;
    private String duelsTotal;
    private String duelsWon;
    private String dribbleAttempts;
    private String dribbleSucc;
    private String penComm;
    private String penWon;
    private String penScored;
    private String penMissed;
    private String passes;
    private String passesAccuracy;
    private String keyPasses;
    private String woordworks;
    private String rating;

    public PlayerStat(PlayerDTO playerDTO) {
        this.playerId = playerDTO.getPlayer_key();
        this.matchPlayed = playerDTO.getPlayer_match_played();
        this.goals = playerDTO.getPlayer_goals();
        this.yellowCards = playerDTO.getPlayer_yellow_cards();
        this.redCards = playerDTO.getPlayer_red_cards();
        this.injured = playerDTO.getPlayer_injured();
        this.substituteOut = playerDTO.getPlayer_substitute_out();
        this.substitutesOnBench = playerDTO.getPlayer_substitutes_on_bench();
        this.assists = playerDTO.getPlayer_assists();
        this.isCaptain = playerDTO.getPlayer_is_captain();
        this.shotsTotal = playerDTO.getPlayer_shots_total();
        this.goalsConceded = playerDTO.getPlayer_goals_conceded();
        this.foulsCommitted = playerDTO.getPlayer_fouls_committed();
        this.tackles = playerDTO.getPlayer_tackles();
        this.blocks = playerDTO.getPlayer_blocks();
        this.crossesTotal = playerDTO.getPlayer_crosses_total();
        this.interceptions = playerDTO.getPlayer_interceptions();
        this.clearances = playerDTO.getPlayer_clearances();
        this.dispossesed = playerDTO.getPlayer_dispossesed();
        this.saves = playerDTO.getPlayer_saves();
        this.insideBoxSaves = playerDTO.getPlayer_inside_box_saves();
        this.duelsTotal = playerDTO.getPlayer_duels_total();
        this.duelsWon = playerDTO.getPlayer_duels_won();
        this.dribbleAttempts = playerDTO.getPlayer_dribble_attempts();
        this.dribbleSucc = playerDTO.getPlayer_dribble_succ();
        this.penComm = playerDTO.getPlayer_pen_comm();
        this.penWon = playerDTO.getPlayer_pen_won();
        this.penScored = playerDTO.getPlayer_pen_scored();
        this.penMissed = playerDTO.getPlayer_pen_missed();
        this.passes = playerDTO.getPlayer_passes();
        this.passesAccuracy = playerDTO.getPlayer_passes_accuracy();
        this.keyPasses = playerDTO.getPlayer_key_passes();
        this.woordworks = playerDTO.getPlayer_woordworks();
        this.rating = playerDTO.getPlayer_rating();
    }

}