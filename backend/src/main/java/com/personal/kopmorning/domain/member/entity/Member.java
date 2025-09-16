package com.personal.kopmorning.domain.member.entity;

import com.personal.kopmorning.domain.article.article.entity.Article;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column
    private String password;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String provider_id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime deleteAt;

    @CreatedDate
    private LocalDateTime created_at;

    @LastModifiedDate
    private LocalDateTime updated_at;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    public Member(String username, String encodePassword, Role role) {
        this.email = username;
        this.name = username;
        this.nickname = username;
        this.provider = username;
        this.password = encodePassword;
        this.provider_id = encodePassword;
        this.role = role;
        this.status = MemberStatus.ADMIN;
    }

    public void withdraw() {
        this.status = MemberStatus.DELETED;
        this.deleteAt = LocalDateTime.now();
    }

    public void isActive() {
        this.status = MemberStatus.ACTIVE;
        this.deleteAt = null;
    }
}
