package com.disi.backend.service;

import com.disi.backend.entity.Photo;
import com.disi.backend.entity.User;
import com.disi.backend.repository.PhotoRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.CreatePhotoRequest;
import com.disi.backend.requests.PhotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    public PhotoResponse createPhoto(CreatePhotoRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Photo photo = new Photo();
        photo.setUploadedBy(user);
        photo.setFileUrl(request.getFileUrl());
        photo.setCaption(request.getCaption());

        Photo savedPhoto = photoRepository.save(photo);
        return new PhotoResponse(savedPhoto);
    }

    public PhotoResponse getPhoto(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));
        return new PhotoResponse(photo);
    }

    public List<PhotoResponse> getUserPhotos(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return photoRepository.findByUploadedBy(user).stream()
                .map(PhotoResponse::new)
                .toList();
    }
}
