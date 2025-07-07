package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findAllByOrderByGoalsDesc();

    @Query(value = "SELECT * FROM ranking ORDER BY goals + assists DESC", nativeQuery = true)
    List<Ranking> findAllOrderByGoalPlusAssistNative();
}
