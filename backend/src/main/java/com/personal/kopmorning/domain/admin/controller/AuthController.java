package com.personal.kopmorning.domain.admin.controller;

import com.personal.kopmorning.domain.admin.responseCode.AdminErrorCode;
import com.personal.kopmorning.domain.admin.responseCode.AdminSuccessCode;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.jwt.TokenDto;
import com.personal.kopmorning.global.jwt.TokenService;
import com.personal.kopmorning.global.security.PrincipalDetails;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @PostMapping(value = "/login")
    public RsData<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail());
        log.info("멤버 정보: {}", member.toString());
        if (member == null || request.getPassword() == null || member.getPassword() == null || !passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(
                    AdminErrorCode.LOGIN_FAIL.getCode(),
                    AdminErrorCode.LOGIN_FAIL.getMessage(),
                    AdminErrorCode.LOGIN_FAIL.getHttpStatus()
            );
        }
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        TokenDto tokenDto = tokenService.generateToken(authentication);

        cookieUtil.addAccessCookie(tokenDto.getAccessToken(), response);
        cookieUtil.addRefreshCookie(tokenDto.getRefreshToken(), response);

        return new RsData<>(
                AdminSuccessCode.LOGIN_SUCCESS.getCode(),
                AdminSuccessCode.LOGIN_SUCCESS.getMessage()
        );
    }
}


