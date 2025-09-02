package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandingRepository extends JpaRepository<Standing, Long> {
    List<Standing> findAllByOrderByPositionDesc();
}
