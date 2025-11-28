package com.personal.kopmorning.Article.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentCreate;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentUpdate;
import com.personal.kopmorning.domain.article.comment.dto.response.ArticleCommentResponse;
import com.personal.kopmorning.domain.article.comment.dto.response.CommentsResponse;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.article.comment.service.ArticleCommentService;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.service.MemberService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private MemberService memberService;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;

    @InjectMocks
    private ArticleCommentService articleCommentService;

    private Member stubMember;
    private Article stubArticle;
    private ArticleComment stubComment;

    @BeforeEach
    void setUp() {
        stubMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .email("hong@example.com")
                .nickname("길동이")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build();

        stubArticle = Article.builder()
                .id(100L)
                .title("테스트 게시물")
                .body("내용입니다")
                .member(stubMember)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        stubComment = ArticleComment.builder()
                .id(10L)
                .article(stubArticle)
                .member(stubMember)
                .body("댓글입니다")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_success() {
        // given
        ArticleCommentCreate req = ArticleCommentCreate.builder()
                .articleId(stubArticle.getId())
                .body("댓글입니다")
                .build();

        when(memberService.getCurrentMember()).thenReturn(stubMember);
        when(articleRepository.findById(stubArticle.getId())).thenReturn(Optional.of(stubArticle));
        when(articleCommentRepository.save(any())).thenAnswer(inv -> {
            ArticleComment saved = inv.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        // when
        ArticleCommentResponse resp = articleCommentService.create(req);

        // then
        assertThat(resp.getBody()).isEqualTo("댓글입니다");
        verify(articleCommentRepository).save(any());
    }

    @Test
    @DisplayName("댓글 목록 조회")
    void getComments_success() {
        // given
        Long articleId = stubArticle.getId();
        int size = 10;

        when(articleCommentRepository.findByArticleIdOrderByIdDesc(articleId, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"))))
                .thenReturn(List.of(stubComment));

        // when
        CommentsResponse result = articleCommentService.getList(articleId, null, size);

        // then
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getBody()).isEqualTo("댓글입니다");
        assertThat(result.getNextCursor()).isEqualTo(stubComment.getId());

        verify(articleCommentRepository, times(1))
                .findByArticleIdOrderByIdDesc(articleId, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id")));
    }


    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() {
        // given
        ArticleCommentUpdate req = ArticleCommentUpdate.builder()
                .articleCommentId(stubComment.getId())
                .body("수정된 댓글입니다")
                .build();

        when(articleCommentRepository.findById(req.getArticleCommentId())).thenReturn(Optional.of(stubComment));
        when(memberService.getCurrentMember()).thenReturn(stubMember);

        // when
        articleCommentService.update(req);

        // then
        assertThat(stubComment.getBody()).isEqualTo("수정된 댓글입니다");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() {
        // given
        when(articleCommentRepository.findById(stubComment.getId())).thenReturn(Optional.of(stubComment));
        when(memberService.getCurrentMember()).thenReturn(stubMember);

        // when
        articleCommentService.delete(stubComment.getId());

        // then
        verify(articleCommentRepository).delete(stubComment);
    }
}
