package com.personal.kopmorning.domain.article.service;

import com.personal.kopmorning.domain.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.entity.Article;
import com.personal.kopmorning.domain.article.entity.Category;
import com.personal.kopmorning.domain.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberNotFoundException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public ArticleResponse addArticle(ArticleCreate articleCreate) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new MemberNotFoundException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage()
                ));

        Article article = Article.builder()
                .title(articleCreate.getTitle())
                .body(articleCreate.getBody())
                .member(member)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(Category.valueOf(articleCreate.getCategory()))
                .likeCount(0L)
                .build();

        articleRepository.save(article);

        return new ArticleResponse(article);
    }

    public ArticleResponse getArticleOne(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물 입니다."));

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
}
