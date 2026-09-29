package com.disi.backend.service;

import com.disi.backend.dto.AlbumRequest;
import com.disi.backend.dto.AlbumResponse;
import com.disi.backend.entity.Album;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.repository.AlbumRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationService authorizationService;

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public AlbumResponse createAlbum(String email, AlbumRequest request) {
        User user = resolveUser(email);
        Album album = new Album();
        album.setUserId(user.getUserId());
        album.setName(request.getName());
        return new AlbumResponse(albumRepository.save(album));
    }

    public List<AlbumResponse> getUserAlbums(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return albumRepository.findByUserId(userId).stream()
                .map(AlbumResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlbumResponse updateAlbum(Long albumId, String email, AlbumRequest request) {
        if (!authorizationService.canOperateAlbum(email, albumId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this album");
        }
        Album album = albumRepository.findById(albumId).get();
        album.setName(request.getName());
        album.setUpdatedAt(LocalDateTime.now());
        return new AlbumResponse(albumRepository.save(album));
    }

    @Transactional
    public void deleteAlbum(Long albumId, String email) {
        if (!authorizationService.canOperateAlbum(email, albumId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this album");
        }
        Album album = albumRepository.findById(albumId).get();
        postRepository.unlinkPostsFromAlbum(albumId);
        albumRepository.delete(album);
    }

    public List<PostResponse> getAlbumPosts(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));
        return postRepository.findByAlbum(album).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse addPostToAlbum(Long albumId, Long postId, String email) {
        if (!authorizationService.canOperateAlbum(email, albumId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this album");
        }
        Album album = albumRepository.findById(albumId).get();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        User user = resolveUser(email);
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this post");
        }

        post.setAlbum(album);
        return new PostResponse(postRepository.save(post));
    }
}