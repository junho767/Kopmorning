package com.personal.kopmorning.domain.article.like.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.article.responseCode.ArticleErrorCode;
import com.personal.kopmorning.domain.article.like.entity.ArticleLike;
import com.personal.kopmorning.domain.article.like.repository.ArticleLikeRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean handleLike(Long articleId) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        ArticleLike articleLike = new ArticleLike(article, member);

        boolean checkExists = articleLikeRepository.existsByArticleIdAndMemberId(articleId, member.getId());

        // todo : redis 분산 락로 동시성 이슈 해결 예정
        if (!checkExists) {
            article.increaseLikeCount();
            articleLikeRepository.save(articleLike);
            return true;
        } else {
            articleLikeRepository.deleteByArticleIdAndMemberId(articleId, member.getId());
            article.decreaseLikeCount();
            return false;
        }
    }
}
