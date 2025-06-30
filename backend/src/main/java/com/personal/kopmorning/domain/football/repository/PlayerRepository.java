package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
