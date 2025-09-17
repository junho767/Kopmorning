package com.personal.kopmorning.domain.article.article.repository;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 첫 페이지: 카테고리 없이 최신순
    Page<Article> findAll(Pageable pageable);

    // 커서 페이지: 카테고리 없이 최신순
    List<Article> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);

    // 첫 페이지: 카테고리 필터
    Page<Article> findByCategory(Category category, Pageable pageable);

    // 커서 페이지: 카테고리 필터
    List<Article> findByCategoryAndIdLessThanOrderByIdDesc(Category category, Long id, Pageable pageable);
}
