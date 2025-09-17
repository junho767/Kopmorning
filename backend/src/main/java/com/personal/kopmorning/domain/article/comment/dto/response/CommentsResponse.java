package com.personal.kopmorning.domain.article.comment.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CommentsResponse {
    private List<ArticleCommentResponse> comments;
    private Long nextCursor;
    private int totalComment;

    public CommentsResponse(List<ArticleCommentResponse> comments, Long nextCursor, int totalComment) {
        this.comments = comments;
        this.nextCursor = nextCursor;
        this.totalComment = totalComment;
    }
}
