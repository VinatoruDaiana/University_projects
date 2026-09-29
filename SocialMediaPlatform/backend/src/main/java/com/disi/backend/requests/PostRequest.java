package com.disi.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private String content;
    private Long photoId;
    private String caption;
}