package com.personal.kopmorning.member.service;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;
    private Member member = new Member();

    @BeforeEach
    void setup() {
        member = new Member();
        member.setRole(Role.USER);
        member.setId(1L);
        member.setNickname("테스트 유저");

        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    }

    @Test
    @DisplayName("회원 정보 수정에 성공한다.")
    void updateMemberProfile_success() {
        // given
        MemberProfileUpdate dto = new MemberProfileUpdate();
        dto.setNickname("테스트 멤버");

        // when
        memberService.update(dto);

        // then
        assertThat(member.getNickname()).isEqualTo("테스트 멤버");
        assertThat(member.getUpdated_at()).isNotNull();
    }

    @Test
    @DisplayName("회원 탈퇴 신청에 성공한다.")
    void requestMemberDeletion_success() {
        // when
        memberService.deleteRequest();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DELETED);
        assertThat(member.getDeleteAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 탈퇴 철회에 성공한다.")
    void cancelMemberDeletion_success() {
        // given
        member.withdraw(); // 탈퇴 상태로 변경

        // when
        memberService.deleteCancel();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDeleteAt()).isNull();
    }
}
