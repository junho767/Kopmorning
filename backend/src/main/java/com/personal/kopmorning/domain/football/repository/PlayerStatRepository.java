package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {
    PlayerStat findByPlayerId(Long playerId);
}
