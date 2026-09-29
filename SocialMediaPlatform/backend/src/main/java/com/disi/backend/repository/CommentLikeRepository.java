package com.disi.backend.repository;

import com.disi.backend.entity.Comment;
import com.disi.backend.entity.CommentLike;
import com.disi.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentAndUser(Comment comment, User user);
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    int countByComment(Comment comment);
}