package com.personal.kopmorning.global.jwt;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberException;
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
    private static final String BLACKLIST_A_PREFIX = "blackList:a ";
    private static final String BLACKLIST_R_PREFIX = "blackList:r ";

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
        Member member = memberRepository.findByEmail(email);
        Role role = member != null ? member.getRole() : Role.USER;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        Member member = memberRepository.findByEmail(email);
        Role role = member != null ? member.getRole() : Role.USER;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_MISSING.getCode(),
                    MemberErrorCode.TOKEN_MISSING.getMessage(),
                    MemberErrorCode.TOKEN_MISSING.getHttpStatus()
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
            throw e;
        } catch (JwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_INVALID.getCode(),
                    MemberErrorCode.TOKEN_INVALID.getMessage(),
                    MemberErrorCode.TOKEN_INVALID.getHttpStatus()
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
            throw new TokenException(
                    MemberErrorCode.TOKEN_EXPIRE.getCode(),
                    MemberErrorCode.TOKEN_EXPIRE.getMessage(),
                    MemberErrorCode.TOKEN_EXPIRE.getHttpStatus()
            );
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_INVALID.getCode(),
                    MemberErrorCode.TOKEN_INVALID.getMessage(),
                    MemberErrorCode.TOKEN_INVALID.getHttpStatus()
            );
        }
    }

    // refreshToken redis 에 블랙리스트로 저장
    public void addToBlacklist(String accessToken, String refreshToken, long accessTokenTtl, long refreshTokenTtl) {
        String accessTokenKey = BLACKLIST_A_PREFIX + accessToken;
        String refreshTokenKey = BLACKLIST_R_PREFIX + refreshToken;

        redisTemplate.opsForValue().set(accessTokenKey, accessToken, accessTokenTtl, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, refreshTokenTtl, TimeUnit.MILLISECONDS);
    }

    // accessToken 이 블랙리스트에 있는지 확인
    public boolean isAccessTokenBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_A_PREFIX + accessToken);
    }

    // refreshToken 이 블랙리스트에 있는지 확인
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        return redisTemplate.hasKey(BLACKLIST_R_PREFIX + refreshToken);
    }

    // 토큰에서 이메일(Subject) 추출
    public String extractEmail(String token) {
        String email = parseClaims(token).getSubject();

        if (!memberRepository.existsByEmail(email)) {
            throw new MemberException(
                    MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                    MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                    MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
            );
        }
        return email;
    }

    // 토큰에서 만료 시간 추출
    public long getRemainingTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis(); // 남은 시간 밀리초
    }

    // refreshToken 을 이용한 accessToken 재발급
    public TokenDto reissueRefreshToken(String refreshToken) {
        try {
            validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getCode(),
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getMessage(),
                    MemberErrorCode.TOKEN_REFRESH_EXPIRE.getHttpStatus()
            );
        } catch (JwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_INVALID.getCode(),
                    MemberErrorCode.TOKEN_INVALID.getMessage(),
                    MemberErrorCode.TOKEN_INVALID.getHttpStatus()
            );
        }

        String email = extractEmail(refreshToken);
        String accessToken = createAccessToken(email);

        return new TokenDto(accessToken, refreshToken, BEARER_TYPE);
    }
}

