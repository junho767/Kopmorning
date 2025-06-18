package com.personal.kopmorning.domain.article.service;

import com.personal.kopmorning.domain.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.dto.request.ArticleUpdate;
import com.personal.kopmorning.domain.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.entity.Article;
import com.personal.kopmorning.domain.article.entity.Category;
import com.personal.kopmorning.domain.article.repository.ArticleRepository;
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
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    private final static Long INIT_COUNT = 0L;

    public ArticleResponse addArticle(ArticleCreate articleCreate) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
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

        return new ArticleResponse(article);
    }

    @Transactional
    public ArticleResponse getArticleOne(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        article.setViewCount(article.getViewCount() + 1);

        return new ArticleResponse(article);
    }

    public ArticleListResponse getArticleListByCategory(String category) {
        List<Article> articleList = articleRepository.findByCategory(Category.valueOf(category));

        List<ArticleResponse> articles = articleList.stream()
                .map(ArticleResponse::new)
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
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
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
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
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
}
