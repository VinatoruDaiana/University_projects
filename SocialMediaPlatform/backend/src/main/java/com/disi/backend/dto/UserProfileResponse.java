package com.disi.backend.dto;

import com.disi.backend.entity.User;
import com.disi.backend.entity.UserProfile;

public class UserProfileResponse {
    public String id;
    public String username;
    public String email;
    public String role;
    public String firstName;
    public String lastName;
    public String bio;
    public String birthdate;
    public String location;
    public String photoUrl;
    public String status;
    public Boolean isBanned;

    public UserProfileResponse(User user, UserProfile profile) {
        this.id = user.getUserId().toString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();

        // Null-safe handling to avoid NullPointerException during search
        if (profile != null) {
            this.firstName = profile.getFirstName();
            this.lastName = profile.getLastName();
            this.bio = profile.getBio();
            this.birthdate = profile.getBirthDate() != null ? profile.getBirthDate().toString() : null;
            this.location = profile.getLocation();
            this.photoUrl = profile.getProfilePhotoUrl();
            this.status = user.getStatus() != null ? user.getStatus() : (Boolean.TRUE.equals(user.getIsBanned()) ? "BLOCKED" : "ACTIVE");
            this.isBanned = user.getIsBanned();
    }
    }


}
