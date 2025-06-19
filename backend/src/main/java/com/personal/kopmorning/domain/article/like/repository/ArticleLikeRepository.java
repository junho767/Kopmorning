package com.personal.kopmorning.domain.article.like.repository;

import com.personal.kopmorning.domain.article.like.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleIdAndMemberId(Long article_id, Long member_id);
    void deleteByArticleIdAndMemberId(Long article_id, Long member_id);
}
