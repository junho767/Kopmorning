package com.personal.kopmorning.member.service;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

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
    }

    @Test
    @DisplayName("회원 정보 수정에 성공한다.")
    void updateMemberProfile_success() {
        // given
        MemberProfileUpdate dto = new MemberProfileUpdate();
        dto.setNickname("테스트 멤버");

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentMember).thenReturn(member);

            // when
            memberService.update(dto);

            // then
            assertThat(member.getNickname()).isEqualTo("테스트 멤버");
            assertThat(member.getUpdated_at()).isNotNull();
        }
    }

    @Test
    @DisplayName("회원 탈퇴 신청에 성공한다.")
    void requestMemberDeletion_success() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentMember).thenReturn(member);

            // when
            memberService.deleteRequest();

            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.DELETED);
            assertThat(member.getDeleteAt()).isNotNull();
        }
    }

    @Test
    @DisplayName("회원 탈퇴 철회에 성공한다.")
    void cancelMemberDeletion_success() {
        // given
        member.withdraw(); // 탈퇴 상태로 변경

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentMember).thenReturn(member);

            // when
            memberService.deleteCancel();

            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
            assertThat(member.getDeleteAt()).isNull();
        }
    }
}
