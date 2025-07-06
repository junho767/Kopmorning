package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.MatchDTO;
import com.personal.kopmorning.global.utils.TimeUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    private Long id;

    private String status;
    private Long matchDay;
    private String gameTime;

    private Long competitionId;
    private String competitionName;

    private Long homeTeamId;
    private String homeTeamName;

    private Long awayTeamId;
    private String awayTeamName;

    private Long homeScore;
    private Long awayScore;

    private Long homeScoreHalf;
    private Long awayScoreHalf;

    public Game(MatchDTO.Match dto) {
        this.id = dto.id();
        this.gameTime = TimeUtil.convertUtcToKst(dto.utcDate());
        this.status = dto.status();
        this.matchDay = dto.matchday();
        this.competitionId = dto.competition().id();
        this.competitionName = dto.competition().name();
        this.homeTeamId = dto.homeTeam().id();
        this.homeTeamName = dto.homeTeam().name();
        this.awayTeamId = dto.awayTeam().id();
        this.awayTeamName = dto.awayTeam().name();

        if (dto.score() != null) {
            if (dto.score().fullTime() != null) {
                this.homeScore = dto.score().fullTime().home();
                this.awayScore = dto.score().fullTime().away();
            }
            if (dto.score().halfTime() != null) {
                this.homeScoreHalf = dto.score().halfTime().home();
                this.awayScoreHalf = dto.score().halfTime().away();
            }
        }
    }

}
