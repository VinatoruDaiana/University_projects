package com.disi.backend.dto;

import com.disi.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;

    public UserDTO(User user) {
        this.userId = user.getUserId();
    }
}