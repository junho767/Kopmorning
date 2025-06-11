package com.personal.kopmorning.domain.member.repository;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
    List<Member> findAllByStatusAndDeleteAtBefore(Member_Status status, LocalDateTime dateTime);

    boolean existsByEmail(String email);
}
