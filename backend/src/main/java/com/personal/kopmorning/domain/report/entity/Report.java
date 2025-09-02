package com.personal.kopmorning.domain.report.entity;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Article article;

    @ManyToOne
    private ArticleComment articleComment;

    @ManyToOne
    private Member member;

    private String reason;

    @CreatedDate
    private LocalDateTime reportedAt;

    public Report(Article article, Member member, String reason) {
        this.article = article;
        this.member = member;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now();
    }

    public Report(ArticleComment comment, Member member, String reason) {
        this.articleComment = comment;
        this.member = member;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now();
    }
}
