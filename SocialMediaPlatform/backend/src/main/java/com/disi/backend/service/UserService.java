package com.disi.backend.service;

import com.disi.backend.dto.UserProfileResponse;
import com.disi.backend.entity.Album;
import com.disi.backend.entity.Photo;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.entity.UserProfile;
import com.disi.backend.repository.AlbumRepository;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.PhotoRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserProfileRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public List<UserProfileResponse> getAllUsersWithProfiles() {
        return userRepository.findAll().stream().map(user -> {
            UserProfile profile = userProfileRepository.findByUserId(user.getUserId())
                    .orElseGet(() -> {
                        UserProfile p = new UserProfile();
                        p.setUserId(user.getUserId());
                        return p;
                    });
            return new UserProfileResponse(user, profile);
        }).toList();
    }

    public void banUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already banned");
        }
        user.setIsBanned(true);
        user.setStatus("BLOCKED");
        userRepository.save(user);
        mailService.sendBanNotification(user.getEmail(), user.getUsername(), reason);
    }

    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!Boolean.TRUE.equals(user.getIsBanned())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not banned");
        }
        user.setIsBanned(false);
        user.setStatus("ACTIVE");
        userRepository.save(user);
        mailService.sendUnbanNotification(user.getEmail(), user.getUsername());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user);
        List<Photo> postPhotos = posts.stream()
                .filter(p -> p.getPhoto() != null)
                .map(Post::getPhoto)
                .toList();
        postRepository.deleteAll(posts);
        photoRepository.deleteAll(postPhotos);

        List<Photo> userPhotos = photoRepository.findByUploadedBy(user);
        photoRepository.deleteAll(userPhotos);

        List<Album> albums = albumRepository.findByUserId(userId);
        for (Album album : albums) {
            postRepository.unlinkPostsFromAlbum(album.getAlbumId());
        }
        albumRepository.deleteAll(albums);

        friendshipRepository.deleteAllByUser(user);

        userProfileRepository.findByUserId(userId).ifPresent(userProfileRepository::delete);

        userRepository.delete(user);
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request, String authenticatedEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getBirthdate() != null && !request.getBirthdate().isEmpty()) profile.setBirthDate(LocalDate.parse(request.getBirthdate()));
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getPhotoUrl() != null) profile.setProfilePhotoUrl(request.getPhotoUrl());

        userProfileRepository.save(profile);
        return new UserProfileResponse(user, profile);
    }

    public UserProfileResponse getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getUserId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(user.getUserId());
                    return newProfile;
                });

        return new UserProfileResponse(user, profile);
    }

    public UserProfileResponse getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        return new UserProfileResponse(user, profile);
    }

    public UserProfileResponse uploadProfilePhoto(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only jpg, jpeg, and png files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            Path uploadPath = Paths.get(uploadDir, "profile-photos");
            Files.createDirectories(uploadPath);

            String filename = userId + "_" + System.currentTimeMillis() + "." + ext;
            Files.write(uploadPath.resolve(filename), file.getBytes());

            String photoUrl = "/api/uploads/profile-photos/" + filename;

            UserProfile profile = userProfileRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        UserProfile newProfile = new UserProfile();
                        newProfile.setUserId(userId);
                        return newProfile;
                    });

            profile.setProfilePhotoUrl(photoUrl);
            userProfileRepository.save(profile);

            return new UserProfileResponse(user, profile);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }
    }

    public List<UserProfileResponse> searchUsers(String query, String currentUserEmail) {
        // Limit results to top 10 matches for performance and UX
        Pageable limit = PageRequest.of(0, 10);
        List<Object[]> results = userRepository.searchUsersByQuery(query, currentUserEmail, limit);

        return results.stream().map(obj -> {
            User user = (User) obj[0];
            UserProfile profile = (UserProfile) obj[1]; // Can be null if profile not yet created

            return new UserProfileResponse(user, profile);
        }).collect(Collectors.toList());
    }
}