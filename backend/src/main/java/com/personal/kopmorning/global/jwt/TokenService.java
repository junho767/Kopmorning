package com.personal.kopmorning.global.jwt;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberNotFoundException;
import com.personal.kopmorning.global.exception.security.TokenException;
import com.personal.kopmorning.global.security.PrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenService {
    private final Key key;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BEARER_TYPE = "Bearer";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BLACKLIST_PREFIX = "blackList:";

    @Value("${jwt.expiration.access-token}")
    private int accessTokenExpiration;
    @Value("${jwt.expiration.refresh-token}")
    private int refreshTokenExpiration;


    public TokenService(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository, RedisTemplate<String, String> redisTemplate) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Authentication authentication) {
        String accessToken = createAccessToken(authentication.getName());
        String refreshToken = createRefreshToken(authentication.getName());

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, Role.USER)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, Role.USER)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // todo : 예외 처리 확실하게 해야함.
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_MISSING.getCode(),
                    MemberErrorCode.TOKEN_MISSING.getMessage()
            );
        }

        Member member = memberRepository.findByEmail(claims.getSubject());
        UserDetails principal = new PrincipalDetails(member);

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getCode(),
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getMessage()
            );
        } catch (JwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_INVALID.getCode(),
                    MemberErrorCode.TOKEN_INVALID.getMessage()
            );
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(MemberErrorCode.TOKEN_EXPIRE.getCode(), MemberErrorCode.TOKEN_EXPIRE.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new TokenException(MemberErrorCode.TOKEN_INVALID.getCode(), MemberErrorCode.TOKEN_INVALID.getMessage());
        }
    }

    // refreshToken redis 에 블랙리스트로 저장
    public void addToBlacklist(String refreshToken, long expirationTime) {
        String key = BLACKLIST_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, BLACKLIST_PREFIX, expirationTime, TimeUnit.MILLISECONDS);
    }

    // 토큰에서 이메일(Subject) 추출
    public String extractEmail(String token) {
        String email = parseClaims(token).getSubject();

        if (!memberRepository.existsByEmail(email)) {
            throw new MemberNotFoundException(
                    MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                    MemberErrorCode.MEMBER_NOT_FOUND.getMessage()
            );
        }
        return email;
    }

    // refreshToken 의 남은 기간 추출
    public long getExpirationTimeFromToken(String refreshToken) {
        return getRemainingTime(refreshToken);
    }

    // 토큰에서 만료 시간 추출
    public long getRemainingTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis(); // 남은 시간 밀리초
    }

    // refreshToken 을 이용한 accessToken 재발급
    public TokenDto reissueRefreshToken(String refreshToken) {
        String email = extractEmail(refreshToken);

        if (getRemainingTime(refreshToken) <= 0) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getCode(),
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getMessage()
            );
        }

        String accessToken = createAccessToken(email);

        return new TokenDto(accessToken, refreshToken, BEARER_TYPE);
    }
}

