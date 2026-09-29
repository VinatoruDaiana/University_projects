package com.disi.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserInfo user;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String email;
        private String username;
        private String role;
        private String firstName;
        private String lastName;
        private String bio;
        private String birthdate;
        private String location;
        private String photoUrl;
        private String status;
        private Boolean isBanned;
    }
}