package com.disi.backend.service;

import com.disi.backend.dto.PostCreatedNotificationEvent;
import com.disi.backend.entity.Photo;
import com.disi.backend.entity.PostLike;
import com.disi.backend.repository.PhotoRepository;
import com.disi.backend.repository.PostLikeRepository;
import com.disi.backend.requests.LikeResponse;
import com.disi.backend.requests.PostResponse;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.PostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private NotificationProducer notificationProducer;

    public PostResponse createPost(PostRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setContent(request.getContent());

        if (request.getPhotoId() != null) {
            Photo photo = photoRepository.findById(request.getPhotoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));
            post.setPhoto(photo);
        }

        Post savedPost = postRepository.save(post);

        moderationService.analyzeAndReport("POST", savedPost.getId(), user.getUserId(), savedPost.getContent());

        PostCreatedNotificationEvent event = new PostCreatedNotificationEvent(
                user.getUserId(), savedPost.getId(), user.getUsername(), savedPost.getCreatedAt()
        );
        notificationProducer.publishPostCreatedEvent(event);

        return new PostResponse(savedPost);
    }

    public PostResponse getPost(Long id, String email) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exist"));
        return mapToResponseWithLikes(post, email);
    }

    @Transactional
    public void updatePost(Long id, PostRequest request, String email) {
        if (!authorizationService.canOperatePost(email, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this post");
        }
        Post post = postRepository.findById(id).get();
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (request.getPhotoId() != null) {
            Photo photo = photoRepository.findById(request.getPhotoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));
            photo.setCaption(request.getCaption());
            photoRepository.save(photo); // Save the updated caption

            post.setPhoto(photo);
        } else {
            // If the frontend sends null: remove the photo relation
            post.setPhoto(null);
        }

        postRepository.save(post);
    }

    public void deletePost(Long id, String email) {
        if (!authorizationService.canOperatePost(email, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this post");
        }
        Post post = postRepository.findById(id).get();
        Photo photoToDelete = post.getPhoto();
        postRepository.delete(post);

        if (photoToDelete != null) {
            photoRepository.delete(photoToDelete);
        }
    }

    public List<PostResponse> getUserPosts(Long userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user);

        return posts.stream()
                .map(post -> mapToResponseWithLikes(post, email))
                .toList();
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(PostResponse::new)
                .toList();
    }

    public LikeResponse likePost(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post already liked");
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        postLikeRepository.save(like);

        return new LikeResponse(postId, true);
    }

    public void unlikePost(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        PostLike like = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found"));

        postLikeRepository.delete(like);
    }

    public PostResponse mapToResponseWithLikes(Post post, String currentUserEmail) {
        PostResponse response = new PostResponse(post);
        response.setLikeCount((int) postLikeRepository.countByPost(post));

        if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            response.setLikedByCurrentUser(currentUser != null && postLikeRepository.existsByPostAndUser(post, currentUser));
        } else {
            response.setLikedByCurrentUser(false);
        }
        return response;
    }
}