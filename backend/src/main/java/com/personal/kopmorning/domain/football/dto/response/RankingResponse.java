package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.Ranking;
import com.personal.kopmorning.domain.football.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private int rank;
    private Long goals;
    private Long assist;
    private Long penalty;

    private String player_name;
    private String player_position;

    private String team_name;
    private String team_crest;

    public RankingResponse(Ranking ranking, Player player , Team team) {
        this.goals = ranking.getGoals();
        this.assist = ranking.getAssists();
        this.penalty = ranking.getPenalties();
        this.player_name = player.getName();
        this.player_position = player.getPosition();
        this.team_name = team.getName();
        this.team_crest = team.getCrest();
    }
}
