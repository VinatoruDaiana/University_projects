package com.disi.backend.service;

import com.disi.backend.entity.*;
import com.disi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("authorizationService")
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User getUser(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean isAdmin(String authenticatedEmail) {
        User user = getUser(authenticatedEmail);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public boolean isConnected(String authenticatedEmail) {
        User user = getUser(authenticatedEmail);
        return user != null && !Boolean.TRUE.equals(user.getIsBanned());
    }

    public boolean canOperateAccount(String authenticatedEmail, Long targetId) {
        User user = getUser(authenticatedEmail);
        if (user == null || targetId == null) return false;
        if (user.getUserId().equals(targetId)) return true;
        return "ADMIN".equals(user.getRole());
    }

    public boolean canOperatePost(String authenticatedEmail, Long postId) {
        User user = getUser(authenticatedEmail);
        if (user == null || postId == null) return false;
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) return false;

        Long ownerId = postOpt.get().getUser().getUserId();
        return user.getUserId().equals(ownerId) || "ADMIN".equals(user.getRole());
    }

    public boolean canEditComment(String authenticatedEmail, Long commentId) {
        User user = getUser(authenticatedEmail);
        if (user == null || commentId == null) return false;

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) return false;

        Long commentOwnerId = commentOpt.get().getUser().getUserId();
        return user.getUserId().equals(commentOwnerId) || "ADMIN".equals(user.getRole());
    }

    public boolean canDeleteComment(String authenticatedEmail, Long commentId) {
        User user = getUser(authenticatedEmail);
        if (user == null || commentId == null) return false;

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) return false;

        Comment comment = commentOpt.get();
        Long commentOwnerId = comment.getUser().getUserId();
        Long postOwnerId = comment.getPost().getUser().getUserId();

        return user.getUserId().equals(commentOwnerId)
                || user.getUserId().equals(postOwnerId)
                || "ADMIN".equals(user.getRole());
    }

    public boolean canLikePost(String authenticatedEmail, Long postId) {
        User user = getUser(authenticatedEmail);
        if (user == null || postId == null) return false;
        if (Boolean.TRUE.equals(user.getIsBanned())) return false;

        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) return false;

        Long ownerId = postOpt.get().getUser().getUserId();
        return !user.getUserId().equals(ownerId);
    }

    public boolean canOperateAlbum(String authenticatedEmail, Long albumId) {
        User user = getUser(authenticatedEmail);
        if (user == null || albumId == null) return false;
        Optional<Album> albumOpt = albumRepository.findById(albumId);
        if (albumOpt.isEmpty()) return false;

        Long ownerId = albumOpt.get().getUserId();
        return user.getUserId().equals(ownerId) || "ADMIN".equals(user.getRole());
    }

    public boolean canOperatePhoto(String authenticatedEmail, Long photoId) {
        User user = getUser(authenticatedEmail);
        if (user == null || photoId == null) return false;

        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) return false;

        Long ownerId = photoOpt.get().getUploadedBy().getUserId();
        return user.getUserId().equals(ownerId) || "ADMIN".equals(user.getRole());
    }

    public boolean canOperateFriendship(String authenticatedEmail, Long friendshipId) {
        User user = getUser(authenticatedEmail);
        if (user == null || friendshipId == null) return false;
        Optional<Friendship> fOpt = friendshipRepository.findById(friendshipId);
        if (fOpt.isEmpty()) return false;

        Long reqId = fOpt.get().getRequester().getUserId();
        Long addId = fOpt.get().getAddressee().getUserId();

        return user.getUserId().equals(reqId) || user.getUserId().equals(addId) || "ADMIN".equals(user.getRole());
    }
}
