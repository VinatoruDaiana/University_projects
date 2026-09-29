package com.disi.backend.dto;

import com.disi.backend.entity.Album;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlbumResponse {

    private final Long albumId;
    private final String name;
    private final Long userId;
    private final LocalDateTime createdAt;

    public AlbumResponse(Album album) {
        this.albumId = album.getAlbumId();
        this.name = album.getName();
        this.userId = album.getUserId();
        this.createdAt = album.getCreatedAt();
    }
}
