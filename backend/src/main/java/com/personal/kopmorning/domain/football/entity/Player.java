package com.personal.kopmorning.domain.football.entity;


import com.personal.kopmorning.domain.football.dto.response.PlayerDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
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

    public Player(PlayerDTO playerDTO) {
        this.id = playerDTO.getPlayer_key();
        this.player_name = playerDTO.getPlayer_name();
        this.player_complete_name = playerDTO.getPlayer_complete_name();
        this.player_image = playerDTO.getPlayer_image();
        this.player_country = playerDTO.getPlayer_country();
        this.player_number = playerDTO.getPlayer_number();
        this.player_age = playerDTO.getPlayer_age();
        this.player_type = playerDTO.getPlayer_type();
        this.player_birthdate = playerDTO.getPlayer_birthdate();
    }
}
