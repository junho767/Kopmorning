package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.TeamDTO;
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
    private Long teamId;
    private String name;
    private String nationality;
    private String dateOfBirth;
    private String position;

    public Player(TeamDTO.Player player) {
        this.id = player.id();
        this.name = player.name();
        this.nationality = player.nationality();
        this.dateOfBirth = player.dateOfBirth();
        this.position = player.position();
    }
}
