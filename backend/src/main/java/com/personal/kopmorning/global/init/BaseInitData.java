package com.personal.kopmorning.global.init;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.football.entity.Ranking;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.RankingRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class BaseInitData implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final RankingRepository rankingRepository;
    private final ArticleRepository articleRepository;

    @Override
    public void run(ApplicationArguments args) {
        memberInit();
        articleInit();
        playerInit();
    }

    private void memberInit() {
        if (memberRepository.count() > 0) return; // 이미 데이터가 있으면 초기화 스킵

        memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong1@example.com")
                .nickname("길동이")
                .provider("kakao")
                .provider_id("kakao_1001")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("김철수")
                .email("kim2@example.com")
                .nickname("철수짱")
                .provider("google")
                .provider_id("google_1002")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("이영희")
                .email("lee3@example.com")
                .nickname("영희쨩")
                .provider("naver")
                .provider_id("naver_1003")
                .role(Role.ADMIN)
                .status(Member_Status.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("박민수")
                .email("park4@example.com")
                .nickname("민수킹")
                .provider("kakao")
                .provider_id("kakao_1004")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("최지우")
                .email("choi5@example.com")
                .nickname("지우스타")
                .provider("google")
                .provider_id("google_1005")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build());
    }

    private void articleInit() {
        if (articleRepository.count() > 0) return;

        List<Member> members = memberRepository.findAll();

        articleRepository.save(Article.builder()
                .title("프리미어리그 개막전 리뷰")
                .body("맨시티와 아스널의 개막전 경기 리뷰입니다...")
                .likeCount(5L)
                .viewCount(0L)
                .category(Category.FOOTBALL)
                .member(members.get(0))
                .build());

        articleRepository.save(Article.builder()
                .title("농구 드래프트 결과 분석")
                .body("2025 NBA 드래프트에서 가장 주목받은 선수는?")
                .likeCount(3L)
                .viewCount(0L)
                .category(Category.FOOTBALL)
                .member(members.get(1))
                .build());

        articleRepository.save(Article.builder()
                .title("손흥민 인터뷰 정리")
                .body("손흥민 선수의 월드컵 이후 첫 인터뷰 요약입니다.")
                .likeCount(7L)
                .viewCount(0L)
                .category(Category.FOOTBALL)
                .member(members.get(2))
                .build());

        articleRepository.save(Article.builder()
                .title("축구 올스타전 하이라이트")
                .body("2025 EPL 올스타전에서 활약한 선수들!")
                .likeCount(2L)
                .viewCount(0L)
                .category(Category.FOOTBALL)
                .member(members.get(3))
                .build());

        articleRepository.save(Article.builder()
                .title("축구 트레블 우승팀 분석")
                .body("PSG 가 다시 한번 해냈습니다.")
                .likeCount(4L)
                .viewCount(0L)
                .category(Category.FOOTBALL)
                .member(members.get(4))
                .build());
    }

    private void playerInit() {
        List<Ranking> rankings = List.of(
                new Ranking(null, 12L, 7L, 2L, 16347L, 64L),     // Dominik Szoboszlai
                new Ranking(null, 10L, 15L, 1L, 19334L, 64L),    // Florian Wirtz
                new Ranking(null, 9L, 5L, 0L, 22396L, 64L),      // Luis Díaz
                new Ranking(null, 18L, 6L, 3L, 28612L, 64L),     // Darwin Núñez
                new Ranking(null, 6L, 10L, 2L, 45681L, 64L),     // Alexis Mac Allister
                new Ranking(null, 4L, 8L, 0L, 81793L, 64L)       // Ryan Gravenberch
        );

        rankingRepository.saveAll(rankings);
    }
}
