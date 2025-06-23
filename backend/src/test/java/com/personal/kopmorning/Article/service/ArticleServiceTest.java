package com.personal.kopmorning.Article.service;

import com.personal.kopmorning.domain.article.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.article.dto.request.ArticleUpdate;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.article.service.ArticleService;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.global.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ArticleService articleService;
    private Member stubMember;
    private Article stubArticle;

    @BeforeEach
    void setUp() {
        stubMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .email("hong@example.com")
                .nickname("길동이")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build();

        stubArticle = Article.builder()
                .id(10L)
                .title("제목")
                .body("본문")
                .member(stubMember)
                .category(Category.FOOTBALL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0L)
                .viewCount(0L)
                .build();
    }

    @Test
    @DisplayName("게시물 생성")
    void addArticle_success() {
        // given
        ArticleCreate req = ArticleCreate.builder()
                .title("새 글")
                .body("새 본문")
                .category("FOOTBALL")
                .build();

        // SecurityUtil.getRequiredMemberId() → 1L
        try (MockedStatic<SecurityUtil> utilMock = mockStatic(SecurityUtil.class)) {
            utilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

            when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
            when(articleRepository.save(any(Article.class)))
                    .thenAnswer(inv -> {
                        Article saved = inv.getArgument(0);
                        saved.setId(99L);
                        return saved;
                    });

            // when
            ArticleResponse resp = articleService.addArticle(req);

            // then
            assertThat(resp.getId()).isEqualTo(99L);
            assertThat(resp.getTitle()).isEqualTo("새 글");

            // 저장된 Article 값도 검증
            ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
            verify(articleRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("새 글");
            assertThat(captor.getValue().getCategory()).isEqualTo(Category.FOOTBALL);
        }
    }

    @Test
    @DisplayName("단건 조회 + 조회수 증가")
    void getArticleOne_success() {
        // given
        when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

        // when
        ArticleResponse resp = articleService.getArticleOne(10L);

        // then
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(stubArticle.getViewCount()).isEqualTo(1L); // 조회수 0 → 1
    }

    @Test
    @DisplayName("카테고리별 목록 조회")
    void getArticleList_success() {
        // given
        when(articleRepository.findByCategory(Category.FOOTBALL))
                .thenReturn(List.of(stubArticle));

        // when
        ArticleListResponse resp = articleService.getArticleListByCategory("FOOTBALL");

        // then
        assertThat(resp.getTotal()).isEqualTo(1);
        assertThat(resp.getArticles().getFirst().getId()).isEqualTo(stubArticle.getId());
        verify(articleRepository, times(1))
                .findByCategory(Category.FOOTBALL);
    }

    @Nested
    @DisplayName("게시물 수정")
    class UpdateArticle {

        @Test
        @DisplayName("작성자 본인이면 수정 성공")
        void update_success() {
            // given
            ArticleUpdate req = ArticleUpdate.builder()
                    .title("수정 제목")
                    .body("수정 본문")
                    .build();

            try (MockedStatic<SecurityUtil> utilMock = mockStatic(SecurityUtil.class)) {
                utilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

                // when
                articleService.updateArticle(10L, req);

                // then
                assertThat(stubArticle.getTitle()).isEqualTo("수정 제목");
            }
        }

        @Test
        @DisplayName("작성자 불일치")
        void update_fail_notAuthor() {
            // given
            Member other = Member.builder()
                    .id(2L).email("other@example.com").role(Role.USER).build();
            stubArticle.setMember(other); // 글 작성자를 다른 사람으로 바꿔 놓음

            ArticleUpdate req = ArticleUpdate.builder()
                    .title("수정 제목").body("수정 본문").build();

            try (MockedStatic<SecurityUtil> utilMock = mockStatic(SecurityUtil.class)) {
                utilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

                // when + then
                assertThrows(RuntimeException.class,
                        () -> articleService.updateArticle(10L, req));
            }
        }
    }

    @Nested
    @DisplayName("게시물 삭제")
    class DeleteArticle {

        @Test
        @DisplayName("작성자 본인이면 삭제 성공")
        void delete_success() {
            try (MockedStatic<SecurityUtil> utilMock = mockStatic(SecurityUtil.class)) {
                utilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

                // when
                articleService.deleteArticle(10L);

                // then
                verify(articleRepository, times(1)).delete(stubArticle);
            }
        }

        @Test
        @DisplayName("작성자 불일치 → RuntimeException")
        void delete_fail_notAuthor() {
            Member other = Member.builder()
                    .id(2L).email("other@example.com").role(Role.USER).build();
            stubArticle.setMember(other);

            try (MockedStatic<SecurityUtil> utilMock = mockStatic(SecurityUtil.class)) {
                utilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

                assertThrows(RuntimeException.class,
                        () -> articleService.deleteArticle(10L));

                verify(articleRepository, never()).delete(any());
            }
        }
    }
}
