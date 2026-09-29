package com.disi.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeResponse {
    private Long postId;
    private boolean liked;
}