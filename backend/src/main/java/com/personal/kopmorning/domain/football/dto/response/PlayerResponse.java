package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Player;
import lombok.Data;

@Data
public class PlayerResponse {
    private Long id;
    private Long team_id;
    private String player_name;
    private String player_complete_name;
    private String player_image;
    private String player_country;
    private String player_number;
    private String player_age;
    private String player_type;
    private String player_birthdate;

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.team_id = player.getTeamId();
        this.player_name = player.getPlayer_name();
        this.player_complete_name = player.getPlayer_complete_name();
        this.player_image = player.getPlayer_image();
        this.player_country = player.getPlayer_country();
        this.player_number = player.getPlayer_number();
        this.player_age = player.getPlayer_age();
        this.player_type = player.getPlayer_type();
        this.player_birthdate = player.getPlayer_birthdate();
    }
}
