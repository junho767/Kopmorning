package com.personal.kopmorning.global.oauth;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.global.oauth.sns.GoogleInfo;
import com.personal.kopmorning.global.oauth.sns.KaKaoInfo;
import com.personal.kopmorning.global.oauth.sns.NaverInfo;
import com.personal.kopmorning.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserDetailService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private static final String GOOGLE_PREFIX = "google";
    private static final String NAVER_PREFIX = "naver";
    private static final String KaKao_PREFIX = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        OAuthInfo oAuth2UserInfo = null;
        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(GOOGLE_PREFIX)) {
            oAuth2UserInfo = new GoogleInfo(oAuth2User.getAttributes());
        }

        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(NAVER_PREFIX)) {
            oAuth2UserInfo = new NaverInfo(oAuth2User.getAttributes());
        }

        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(KaKao_PREFIX)) {
            oAuth2UserInfo = new KaKaoInfo(oAuth2User.getAttributes());
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getProviderEmail();
        String name = oAuth2UserInfo.getProviderName();

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            member = Member.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .status(Member_Status.ACTIVE)
                    .provider(provider)
                    .provider_id(providerId)
                    .created_at(LocalDateTime.now())
                    .build();

            memberRepository.save(member);
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
