package com.personal.kopmorning.domain.article.article.service;

import com.personal.kopmorning.domain.article.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.article.dto.request.ArticleUpdate;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.like.repository.ArticleLikeRepository;
import com.personal.kopmorning.domain.article.responseCode.ArticleErrorCode;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;

    private final static Long INIT_COUNT = 0L;

    public ArticleResponse addArticle(ArticleCreate articleCreate) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        Article article = Article.builder()
                .title(articleCreate.getTitle())
                .body(articleCreate.getBody())
                .member(member)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(Category.valueOf(articleCreate.getCategory()))
                .likeCount(INIT_COUNT)
                .viewCount(INIT_COUNT)
                .build();

        articleRepository.save(article);

        return new ArticleResponse(article, false);
    }

    @Transactional
    public ArticleResponse getArticleOne(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        Long memberId = SecurityUtil.getNullableMemberId();

        if(memberId == null) {
            article.increaseViewCount();
            return new ArticleResponse(article, false);
        }

        boolean liked = articleLikeRepository.existsByArticleIdAndMemberId(articleId, memberId);

        return new ArticleResponse(article, liked);
    }

    public ArticleListResponse getArticleListByCategory(String category) {
        List<Article> articleList = articleRepository.findByCategory(Category.valueOf(category));
        Long memberId = SecurityUtil.getNullableMemberId();

        // 사용자가 게시물에 좋아요 눌렀는 지 판단
        List<ArticleResponse> articles = articleList.stream()
                .map(article -> {
                    boolean liked = false;
                    if (memberId != null) {
                        liked = articleLikeRepository.existsByArticleIdAndMemberId(article.getId(), memberId);
                    }
                    return new ArticleResponse(article, liked);
                })
                .toList();

        int total = articles.size();

        return ArticleListResponse.builder()
                .articles(articles)
                .total(total)
                .category(category)
                .build();
    }

    @Transactional
    public void updateArticle(Long id, ArticleUpdate articleUpdate) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        if (!member.getEmail().equals(article.getMember().getEmail())) {
            throw new ArticleException(
                    ArticleErrorCode.NOT_AUTHOR.getCode(),
                    ArticleErrorCode.NOT_AUTHOR.getMessage(),
                    ArticleErrorCode.NOT_AUTHOR.getHttpStatus()
            );
        }

        article.setTitle(articleUpdate.getTitle());
        article.setBody(articleUpdate.getBody());
    }

    public void deleteArticle(Long id) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        if (!member.getEmail().equals(article.getMember().getEmail())) {
            throw new ArticleException(
                    ArticleErrorCode.NOT_AUTHOR.getCode(),
                    ArticleErrorCode.NOT_AUTHOR.getMessage(),
                    ArticleErrorCode.NOT_AUTHOR.getHttpStatus()
            );
        }

        articleRepository.delete(article);
    }

    @Transactional
    public void forceDeleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        articleRepository.delete(article);
    }
}
