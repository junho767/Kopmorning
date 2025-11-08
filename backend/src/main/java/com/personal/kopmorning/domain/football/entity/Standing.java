package com.personal.kopmorning.domain.football.entity;

import com.personal.kopmorning.domain.football.dto.StandingDTO;
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
public class Standing {
    @Id
    private Long id;
    private Long position;
    private Long playedGames;
    private Long won;
    private Long draw;
    private Long lost;
    private Long points;
    private Long goalsFor;
    private Long goalsAgainst;
    private Long goalsDifference;

    public Standing(StandingDTO.Table table) {
        this.id = table.team().id();
        this.position = table.position();
        this.playedGames = table.playedGames();
        this.won = table.won();
        this.draw = table.draw();
        this.lost = table.lost();
        this.points = table.points();
        this.goalsFor = table.goalsFor();
        this.goalsAgainst = table.goalsAgainst();
    }
}
