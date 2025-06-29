package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootBallRepository extends JpaRepository<Team, Long> {
}
