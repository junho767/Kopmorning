package com.personal.kopmorning.domain.article.comment.repository;

import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    // 커서 없는 경우
    List<ArticleComment> findByArticleIdOrderByIdDesc(Long articleId, Pageable pageable);
    // 커서가 존재하는 경우
    List<ArticleComment> findByArticleIdAndIdLessThanOrderByIdDesc(Long articleId, Long cursor, Pageable pageable);

}
