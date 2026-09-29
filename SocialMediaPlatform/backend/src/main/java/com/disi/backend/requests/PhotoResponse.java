package com.disi.backend.requests;

import com.disi.backend.entity.Photo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoResponse {
    private Long id;
    private Long uploadedBy;
    private String fileUrl;
    private String caption;

    public PhotoResponse(Photo photo) {
        this.id = photo.getId();
        this.uploadedBy = photo.getUploadedBy().getUserId();
        this.fileUrl = photo.getFileUrl();
        this.caption = photo.getCaption();
    }
}
