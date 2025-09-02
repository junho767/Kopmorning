package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Player;
import lombok.Data;

@Data
public class PlayerResponse {
    private Long id;
    private Long team_id;
    private String player_name;
    private String player_nationality;
    private String player_birthOfDate;
    private String player_position;

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.team_id = player.getTeamId();
        this.player_name = player.getName();
        this.player_nationality = player.getNationality();
        this.player_birthOfDate = player.getDateOfBirth();
        this.player_position = player.getPosition();
    }
}
