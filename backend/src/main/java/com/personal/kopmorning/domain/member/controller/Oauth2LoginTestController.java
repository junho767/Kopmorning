package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class Oauth2LoginTestController {
    @GetMapping
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않음");
        }

        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        return ResponseEntity.ok(userAttributes);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMember() {
        Member member = SecurityUtil.getCurrentMember();
        return ResponseEntity.ok(member.toString());
    }
}