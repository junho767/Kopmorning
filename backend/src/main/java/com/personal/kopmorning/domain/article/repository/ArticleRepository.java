package com.personal.kopmorning.domain.article.repository;

import com.personal.kopmorning.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
