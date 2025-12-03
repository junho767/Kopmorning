package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.RankingDTO;
import jakarta.persistence.Entity;
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
    private Long id;
    private Long goals;
    private Long assists;
    private Long penalties;
    private Long teamId;

    public Ranking(RankingDTO.Scorer scorer) {
        this.id = scorer.player().id();
        this.goals = scorer.goals();
        this.assists = scorer.assists();
        this.penalties = scorer.penalties();
        this.teamId = scorer.team().id();
    }
}
