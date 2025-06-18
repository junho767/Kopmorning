package com.personal.kopmorning.domain.article.repository;

import com.personal.kopmorning.domain.article.entity.Article;
import com.personal.kopmorning.domain.article.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(Category category);
}
