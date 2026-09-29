package com.disi.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "Album name is required")
    @Size(max = 100, message = "Album name must not exceed 100 characters")
    private String name;
}
