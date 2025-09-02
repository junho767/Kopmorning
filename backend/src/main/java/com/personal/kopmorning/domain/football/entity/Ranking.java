package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.RankingDTO;
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
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long goals;
    private Long assists;
    private Long penalties;
    private Long playerId;
    private Long teamId;

    public Ranking(RankingDTO.Scorer scorer) {
        this.goals = scorer.goals();
        this.assists = scorer.assists();
        this.penalties = scorer.penalties();
        this.playerId = scorer.player().id();
        this.teamId = scorer.team().id();
    }
}
