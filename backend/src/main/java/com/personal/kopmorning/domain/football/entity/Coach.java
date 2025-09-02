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
public class Coach {
    @Id
    private Long id;
    private String name;
    private String dateOfBirth;
    private String nationality;
    private String contractStartDate;
    private String contractEndDate;

    public Coach(TeamDTO.Coach coach) {
        this.id = coach.id();
        this.name = coach.name();
        this.dateOfBirth = coach.dateOfBirth();
        this.nationality = coach.nationality();
        this.contractStartDate = coach.contract().start();
        this.contractEndDate = coach.contract().until();
    }
}
