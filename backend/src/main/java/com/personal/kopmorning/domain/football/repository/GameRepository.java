package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
