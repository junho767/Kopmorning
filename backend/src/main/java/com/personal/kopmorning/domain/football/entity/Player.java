package com.personal.kopmorning.domain.football.entity;


import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
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
    public Long id;
    public Long teamId;
    public String name;
    public String dateOfBirth;
    public String nationality;

    public Player(PlayerResponse response) {
        this.id = response.getId();
        this.name = response.getName();
        this.dateOfBirth = response.getDateOfBirth();
        this.nationality = response.getNationality();
    }
}
