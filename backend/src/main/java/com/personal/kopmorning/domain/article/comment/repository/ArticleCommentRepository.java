package com.personal.kopmorning.domain.article.comment.repository;

import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findByArticleId(Long articleId);
}
