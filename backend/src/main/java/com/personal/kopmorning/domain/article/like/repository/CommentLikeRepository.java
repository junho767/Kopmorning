package com.personal.kopmorning.domain.article.like.repository;

import com.personal.kopmorning.domain.article.like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
}
