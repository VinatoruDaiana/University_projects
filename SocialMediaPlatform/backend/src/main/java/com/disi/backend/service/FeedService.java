package com.disi.backend.service;

import com.disi.backend.requests.PostResponse;
import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserProfileRepository;
import com.disi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public List<PostResponse> getFeed(String email, int page, int size) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Friendship> friendships = friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED)
                .stream()
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .collect(Collectors.toList());

        List<User> friends = friendships.stream()
                .map(f -> f.getRequester().getUserId().equals(currentUser.getUserId())
                        ? f.getAddressee()
                        : f.getRequester())
                .collect(Collectors.toList());

        List<User> excludedUsers = new ArrayList<>(friends);
        excludedUsers.add(currentUser);

        List<PostResponse> friendPosts = new ArrayList<>();
        if (!friends.isEmpty()) {
            Page<Post> friendPage = postRepository.findPostsByFriends(
                    friends, PageRequest.of(page, size));
            friendPosts = friendPage.getContent().stream()
                    .map(post -> {
                        PostResponse r = new PostResponse(post);
                        userProfileRepository.findByUserId(post.getUser().getUserId())
                                .ifPresent(p -> r.setProfilePhotoUrl(p.getProfilePhotoUrl()));
                        return r;
                    })
                    .collect(Collectors.toList());
        }

        Page<Post> generalPage = postRepository.findPostsExcludingUsers(
                excludedUsers, PageRequest.of(page, size));
        List<PostResponse> generalPosts = generalPage.getContent().stream()
                .map(post -> {
                    PostResponse r = new PostResponse(post);
                    userProfileRepository.findByUserId(post.getUser().getUserId())
                            .ifPresent(p -> r.setProfilePhotoUrl(p.getProfilePhotoUrl()));
                    return r;
                })
                .collect(Collectors.toList());

        List<PostResponse> feed = new ArrayList<>();
        feed.addAll(friendPosts);
        feed.addAll(generalPosts);

        return feed.stream()
                .sorted(Comparator.comparing(PostResponse::getCreatedAt).reversed())
                .distinct()
                .limit(size)
                .collect(Collectors.toList());
    }
}
