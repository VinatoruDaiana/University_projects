package com.disi.backend.requests;

import com.disi.backend.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final Long userId;
    private final String username;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int likeCount;
    private final boolean likedByCurrentUser;

    public CommentResponse(Comment comment, int likeCount, boolean likedByCurrentUser) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.username = comment.getUser().getUsername();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }
}