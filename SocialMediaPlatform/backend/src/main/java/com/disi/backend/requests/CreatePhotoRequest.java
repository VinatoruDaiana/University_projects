package com.disi.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePhotoRequest {
    private String fileUrl;
    private String caption;
}
