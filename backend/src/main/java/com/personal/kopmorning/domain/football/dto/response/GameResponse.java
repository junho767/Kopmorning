package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Game;
import lombok.Data;

@Data
public class GameResponse {
    private Long matchId;
    private Long matchDay;

    private Long homeTeamId;
    private String homeTeamName;

    private Long awayTeamId;
    private String awayTeamName;

    private Long homeTeamScore;
    private Long awayTeamScore;

    private String status;
    private String gameTime;
    private String competitionName;

    public GameResponse(Game game) {
        this.matchId = game.getId();
        this.matchDay = game.getMatchDay();
        this.homeTeamId = game.getHomeTeamId();
        this.homeTeamName = game.getHomeTeamName();
        this.awayTeamId = game.getAwayTeamId();
        this.awayTeamName = game.getAwayTeamName();
        this.homeTeamScore = game.getHomeScore();
        this.awayTeamScore = game.getAwayScore();
        this.status = game.getStatus();
        this.gameTime = game.getGameTime();
        this.competitionName = game.getCompetitionName();

    }

}
