package com.disi.backend.service;

import com.disi.backend.requests.CommentRequest;
import com.disi.backend.requests.CommentResponse;
import com.disi.backend.entity.Comment;
import com.disi.backend.entity.CommentLike;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.repository.CommentLikeRepository;
import com.disi.backend.repository.CommentRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ModerationService moderationService;

    public CommentResponse createComment(Long postId, CommentRequest request, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        User user = userRepository.findByEmail(email).orElseThrow();

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());
        Comment savedComment = commentRepository.save(comment);
        moderationService.analyzeAndReport("COMMENT", savedComment.getCommentId(), user.getUserId(), savedComment.getContent());
        return new CommentResponse(savedComment, 0, false);
    }

    public List<CommentResponse> getCommentsForPost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        User currentUser = email != null ? userRepository.findByEmail(email).orElse(null) : null;

        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream().map(comment -> {
            int likeCount = commentLikeRepository.countByComment(comment);
            boolean liked = currentUser != null && commentLikeRepository.existsByCommentAndUser(comment, currentUser);
            return new CommentResponse(comment, likeCount, liked);
        })
        .sorted(java.util.Comparator.comparingInt(CommentResponse::getLikeCount).reversed())
        .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, String email) {
        if (!authorizationService.canEditComment(email, commentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit this comment");
        }

        Comment comment = commentRepository.findById(commentId).get();
        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);

        User currentUser = userRepository.findByEmail(email).get();
        int likeCount = commentLikeRepository.countByComment(updatedComment);
        boolean liked = commentLikeRepository.existsByCommentAndUser(updatedComment, currentUser);

        return new CommentResponse(updatedComment, likeCount, liked);
    }

    @Transactional
    public void deleteComment(Long commentId, String email) {
        if (!authorizationService.canDeleteComment(email, commentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this comment");
        }
        // Will cascade delete likes if set up in DB, otherwise delete manually
        Comment comment = commentRepository.findById(commentId).get();
        commentRepository.delete(comment);
    }

    @Transactional
    public void likeComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        User user = userRepository.findByEmail(email).orElseThrow();

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Comment already liked");
        }

        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(user);
        commentLikeRepository.save(like);
    }

    @Transactional
    public void unlikeComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        User user = userRepository.findByEmail(email).orElseThrow();

        CommentLike like = commentLikeRepository.findByCommentAndUser(comment, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found"));

        commentLikeRepository.delete(like);
    }
}