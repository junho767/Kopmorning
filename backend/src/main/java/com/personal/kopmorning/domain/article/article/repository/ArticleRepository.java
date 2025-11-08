package com.personal.kopmorning.domain.article.article.repository;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 카테고리 없이 최신순-Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    Page<Article> findAll(Pageable pageable);

    // 카테고리 없이 최신순-Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);

    // 카테고리 필터-Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    Page<Article> findByCategory(Category category, Pageable pageable);

    // 카테고리 필터-Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByCategoryAndIdLessThanOrderByIdDesc(Category category, Long id, Pageable pageable);

    // 검색어 포함-전체, 첫 페이지 - Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByTitleContainingIgnoreCaseOrderByIdDesc(String titleKeyword, Pageable pageable);

    // 검색어 포함-전체, 커서 - Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(Long cursor, String titleKeyword, Pageable pageable);

    // 검색어 포함-카테고리, 첫 페이지 - Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByCategoryAndTitleContainingIgnoreCaseOrderByIdDesc(Category category, String titleKeyword, Pageable pageable);

    // 검색어 포함-카테고리, 커서 - Member 함께 조회
    @EntityGraph(attributePaths = {"member"})
    List<Article> findByCategoryAndIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(Category category, Long cursor, String titleKeyword, Pageable pageable);
}
