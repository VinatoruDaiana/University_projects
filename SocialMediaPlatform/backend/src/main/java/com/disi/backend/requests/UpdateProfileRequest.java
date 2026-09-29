package com.disi.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private String birthdate;
    private String location;
    private String photoUrl;
}
