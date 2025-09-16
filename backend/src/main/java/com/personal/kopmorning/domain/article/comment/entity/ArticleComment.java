package com.personal.kopmorning.domain.article.comment.entity;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.report.entity.Report;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String body;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "articleComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();


    private Long likeCount;

    public void incrementLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        likeCount--;
    }
}
