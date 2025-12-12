package com.personal.kopmorning.domain.article.comment.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentCreate;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentUpdate;
import com.personal.kopmorning.domain.article.comment.dto.response.ArticleCommentResponse;
import com.personal.kopmorning.domain.article.comment.dto.response.CommentsResponse;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.article.responseCode.ArticleErrorCode;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommentService {
    private final MemberService memberService;

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public CommentsResponse getList(Long articleId, Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<ArticleComment> articleComments;

        int totalComments = articleCommentRepository.findAll().size();

        if (cursor == null) {
            articleComments = articleCommentRepository.findByArticleIdOrderByIdDesc(articleId ,pageable);
        } else {
            articleComments = articleCommentRepository.findByArticleIdAndIdLessThanOrderByIdDesc(articleId, cursor, pageable);
        }

        List<ArticleCommentResponse> articleCommentResponses = articleComments.stream()
                .map(ArticleCommentResponse::new)
                .toList();

        Long nextCursor = articleComments.isEmpty() ? null : articleComments.getLast().getId();

        return new CommentsResponse(articleCommentResponses, nextCursor , totalComments);
    }

    public ArticleCommentResponse create(ArticleCommentCreate articleCommentCreate) {
        Member member = memberService.getCurrentMember();
        Article article = articleRepository.findById(articleCommentCreate.getArticleId())
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        ArticleComment articleComment = ArticleComment.builder()
                .member(member)
                .article(article)
                .body(articleCommentCreate.getBody())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        articleCommentRepository.save(articleComment);

        return new ArticleCommentResponse(articleComment);
    }

    @Transactional
    public void update(ArticleCommentUpdate articleCommentUpdate) {
        ArticleComment articleComment = articleCommentRepository.findById(articleCommentUpdate.getArticleCommentId())
                .orElseThrow(() -> new ArticleException(
                        ArticleErrorCode.INVALID_COMMENT.getCode(),
                        ArticleErrorCode.INVALID_COMMENT.getMessage(),
                        HttpStatus.NOT_FOUND)
                );
        Member currentMember = memberService.getCurrentMember();
        Member member = articleComment.getMember();

        if(!currentMember.getId().equals(member.getId())) {
            throw new ArticleException(
                    ArticleErrorCode.NOT_AUTHOR.getCode(),
                    ArticleErrorCode.NOT_AUTHOR.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }

        articleComment.setBody(articleCommentUpdate.getBody());
    }

    @Transactional
    public void delete(Long commentId) {
        ArticleComment articleComment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new ArticleException(
                        ArticleErrorCode.INVALID_COMMENT.getCode(),
                        ArticleErrorCode.INVALID_COMMENT.getMessage(),
                        HttpStatus.NOT_FOUND)
                );
        Member currentMember = memberService.getCurrentMember();
        Member member = articleComment.getMember();

        if(!currentMember.getId().equals(member.getId())) {
            throw new ArticleException(
                    ArticleErrorCode.NOT_AUTHOR.getCode(),
                    ArticleErrorCode.NOT_AUTHOR.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }

        articleCommentRepository.delete(articleComment);
    }

    @Transactional
    public void forceDeleteComment(Long commentId) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new ArticleException(
                        ArticleErrorCode.INVALID_COMMENT.getCode(),
                        ArticleErrorCode.INVALID_COMMENT.getMessage(),
                        HttpStatus.NOT_FOUND)
                );

        articleCommentRepository.delete(comment);
    }
}
