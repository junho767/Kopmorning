package com.personal.kopmorning.global.scheduler;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberScheduler {
    private final MemberRepository memberRepository;

    // 매일 오전 6시 기준으로 탈퇴 신청 후 7일이 지난 유저의 정보 완전히 삭제
    @Scheduled(cron = "0 0 6 * * *")
    public void MemberDeleteScheduler() {
        LocalDateTime nowToMinusSeven = LocalDateTime.now().minusDays(7);

        List<Member> expiredMember = memberRepository.findAllByStatusAndDeleteAtBefore(MemberStatus.DELETED, nowToMinusSeven);

        memberRepository.deleteAll(expiredMember);
    }
}
