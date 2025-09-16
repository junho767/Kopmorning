package com.personal.kopmorning.global.init;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitial implements ApplicationRunner {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        if (!memberRepository.existsByEmail(adminUsername)) {
            Member admin = new Member(adminUsername, passwordEncoder.encode(adminPassword), Role.ADMIN);
            memberRepository.save(admin);
        }
    }
}
