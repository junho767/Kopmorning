package com.personal.kopmorning.global.init;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.football.entity.Ranking;
import com.personal.kopmorning.domain.football.repository.RankingRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.report.entity.Report;
import com.personal.kopmorning.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class BaseInitData implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final RankingRepository rankingRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Override
    public void run(ApplicationArguments args) {
        memberInit();
        articleInit();
        playerInit();
        reportInit();
    }

    private void memberInit() {
        if (memberRepository.count() > 1) return; // 이미 데이터가 있으면 초기화 스킵

        memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong1@example.com")
                .nickname("길동이")
                .provider("kakao")
                .provider_id("kakao_1001")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("김철수")
                .email("kim2@example.com")
                .nickname("철수짱")
                .provider("google")
                .provider_id("google_1002")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("이영희")
                .email("lee3@example.com")
                .nickname("영희쨩")
                .provider("naver")
                .provider_id("naver_1003")
                .role(Role.ADMIN)
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("박민수")
                .email("park4@example.com")
                .nickname("민수킹")
                .provider("kakao")
                .provider_id("kakao_1004")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("최지우")
                .email("choi5@example.com")
                .nickname("지우스타")
                .provider("google")
                .provider_id("google_1005")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .name("서현우").email("seo6@example.com").nickname("현우짱")
                .provider("kakao").provider_id("kakao_1006").role(Role.USER)
                .status(MemberStatus.ACTIVE).build());
        memberRepository.save(Member.builder()
                .name("한지민").email("han7@example.com").nickname("지민스타")
                .provider("google").provider_id("google_1007").role(Role.USER)
                .status(MemberStatus.ACTIVE).build());
        memberRepository.save(Member.builder()
                .name("강민혁").email("kang8@example.com").nickname("민혁킹")
                .provider("naver").provider_id("naver_1008").role(Role.USER)
                .status(MemberStatus.ACTIVE).build());
        memberRepository.save(Member.builder()
                .name("윤서연").email("yoon9@example.com").nickname("서연이")
                .provider("kakao").provider_id("kakao_1009").role(Role.USER)
                .status(MemberStatus.ACTIVE).build());
        memberRepository.save(Member.builder()
                .name("조하늘").email("jo10@example.com").nickname("하늘짱")
                .provider("google").provider_id("google_1010").role(Role.USER)
                .status(MemberStatus.ACTIVE).build());
    }

    private void articleInit() {
        if (articleRepository.count() > 0) return;

        List<Member> members = memberRepository.findAll();

        Article article1 = articleRepository.save(Article.builder()
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

        commentInit(article1);

        for (int i = 1; i <= 10; i++) {
            articleRepository.save(Article.builder()
                    .title("프리미어리그 경기 리뷰 " + i)
                    .body("이번 주 프리미어리그 경기 " + i + "에 대한 리뷰입니다...")
                    .likeCount((long) (Math.random() * 20)) // 좋아요 랜덤
                    .viewCount((long) (Math.random() * 100)) // 조회수 랜덤
                    .category(Category.FOOTBALL)
                    .member(members.get(i % members.size())) // 멤버를 순환하면서 배정
                    .build());
        }
    }

    private void commentInit(Article article) {
        if (articleCommentRepository.count() > 0) return; // 이미 데이터가 있으면 초기화 스킵

        List<Member> members = memberRepository.findAll();

        for (int i = 1; i <= 15; i++) {
            articleCommentRepository.save(ArticleComment.builder()
                    .article(article)
                    .member(members.get(i % members.size())) // 멤버 순환
                    .body("댓글 내용 " + i)
                    .likeCount((long) (Math.random() * 10)) // 좋아요 랜덤
                    .createdAt(LocalDateTime.now().minusDays(15 - i)) // 생성 시간 점점 과거로
                    .updatedAt(LocalDateTime.now().minusDays(15 - i))
                    .build());
        }
    }

    private void playerInit() {
        List<Ranking> rankings = List.of(
                new Ranking(12L, 7L, 2L, 16347L, 64L),     // Dominik Szoboszlai
                new Ranking(10L, 15L, 1L, 19334L, 64L),    // Florian Wirtz
                new Ranking(9L, 5L, 0L, 22396L, 64L),      // Luis Díaz
                new Ranking(18L, 6L, 3L, 28612L, 64L),     // Darwin Núñez
                new Ranking(6L, 10L, 2L, 45681L, 64L),     // Alexis Mac Allister
                new Ranking( 4L, 8L, 0L, 81793L, 64L)       // Ryan Gravenberch
        );

        rankingRepository.saveAll(rankings);
    }

    private void reportInit() {
        if (reportRepository.count() > 0) return; // 이미 데이터 있으면 스킵

        List<Member> members = memberRepository.findAll();
        List<ArticleComment> comments = articleCommentRepository.findAll();

        for (int i = 1; i <= 10; i++) {
            reportRepository.save(Report.builder()
                    .articleComment(comments.get(i % comments.size())) // 댓글 신고
                    .member(members.get(i % members.size()))
                    .reason("신고 사유 " + i)
                    .reportedAt(LocalDateTime.now().minusDays(10 - i))
                    .build());
        }
    }
}
