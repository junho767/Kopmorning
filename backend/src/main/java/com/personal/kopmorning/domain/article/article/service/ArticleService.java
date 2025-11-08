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
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;

    private final static Long INIT_COUNT = 0L;
    private final static String CATEGORY_IS_NULL = "all";

    public ArticleResponse addArticle(ArticleCreate articleCreate) {
        Member member = SecurityUtil.getCurrentMember();

        Article article = Article.builder()
                .title(articleCreate.getTitle())
                .body(articleCreate.getBody())
                .member(member)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(Category.valueOf(articleCreate.getCategory().toUpperCase()))
                .likeCount(INIT_COUNT)
                .viewCount(INIT_COUNT)
                .build();

        articleRepository.save(article);

        return new ArticleResponse(article, false);
    }

    @Transactional
    public ArticleResponse getArticleOne(Long articleId) {
        Article article = findById(articleId);
        Long memberId = SecurityUtil.getNullableMemberId();

        if (memberId == null) {
            article.increaseViewCount();
            return new ArticleResponse(article, false);
        }

        boolean liked = articleLikeRepository.existsByArticleIdAndMemberId(articleId, memberId);

        return new ArticleResponse(article, liked);
    }

    public ArticleListResponse getArticleListByCategory(String category, Long cursor, int size, String keyword) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Article> articleList;

        boolean isAllCategory = category.equalsIgnoreCase(CATEGORY_IS_NULL);
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (isAllCategory) {
            if (cursor == null) {
                articleList = hasKeyword
                        ? articleRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(keyword, pageable)
                        : articleRepository.findAll(pageable).getContent();
            } else {
                articleList = hasKeyword
                        ? articleRepository.findByIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(cursor, keyword, pageable)
                        : articleRepository.findByIdLessThanOrderByIdDesc(cursor, pageable);
            }
        } else {
            Category cat = Category.valueOf(category.toUpperCase());
            if (cursor == null) {
                articleList = hasKeyword
                        ? articleRepository.findByCategoryAndTitleContainingIgnoreCaseOrderByIdDesc(cat, keyword, pageable)
                        : articleRepository.findByCategory(cat, pageable).getContent();
            } else {
                articleList = hasKeyword
                        ? articleRepository.findByCategoryAndIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(cat, cursor, keyword, pageable)
                        : articleRepository.findByCategoryAndIdLessThanOrderByIdDesc(cat, cursor, pageable);
            }
        }


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

        Long nextCursor = articles.isEmpty() ? null : articles.getLast().getId();

        return ArticleListResponse.builder()
                .articles(articles)
                .nextCursor(nextCursor)
                .category(category)
                .build();
    }

    @Transactional
    public void updateArticle(Long articleId, ArticleUpdate articleUpdate) {
        Member member = SecurityUtil.getCurrentMember();
        Article article = findById(articleId);

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

    public void deleteArticle(Long articleId) {
        Member member = SecurityUtil.getCurrentMember();
        Article article = findById(articleId);

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
        Article article = findById(articleId);
        articleRepository.delete(article);
    }

    public Article findById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );
    }
}
