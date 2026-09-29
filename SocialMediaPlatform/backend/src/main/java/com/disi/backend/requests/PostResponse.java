package com.disi.backend.requests;

import com.disi.backend.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private Long userId;
    private Long photoId;
    private String photoUrl;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String username;
    private String profilePhotoUrl;
    private int likeCount;
    private boolean likedByCurrentUser;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getUserId();

        if (post.getPhoto() != null) {
            this.photoId = post.getPhoto().getId();
            this.photoUrl = post.getPhoto().getFileUrl();
        } else {
            this.photoId = null;
            this.photoUrl = null;
        }

        this.content = post.getContent();
        if (post.getCreatedAt() != null) {
            this.createdAt = post.getCreatedAt().withNano(0).toString();
        }
        if (post.getUpdatedAt() != null) {
            this.updatedAt = post.getUpdatedAt().withNano(0).toString();
        }
        this.username = post.getUser().getUsername();
    }
}
