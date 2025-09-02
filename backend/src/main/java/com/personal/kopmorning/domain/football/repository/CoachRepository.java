package com.personal.kopmorning.domain.football.repository;

import com.personal.kopmorning.domain.football.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
}
