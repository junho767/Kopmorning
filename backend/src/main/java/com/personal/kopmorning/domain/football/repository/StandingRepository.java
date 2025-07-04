package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandingRepository extends JpaRepository<Standing, Long> {
}
