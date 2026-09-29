package com.disi.backend.controller;

import com.disi.backend.requests.CreatePhotoRequest;
import com.disi.backend.requests.PhotoResponse;
import com.disi.backend.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<PhotoResponse> uploadPhoto(@RequestBody CreatePhotoRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(photoService.createPhoto(request, principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<PhotoResponse> getPhoto(@PathVariable Long id) {
        return ResponseEntity.ok(photoService.getPhoto(id));
    }
}