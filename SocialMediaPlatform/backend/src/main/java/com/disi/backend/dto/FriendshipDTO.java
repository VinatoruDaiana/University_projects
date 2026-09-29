package com.disi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendshipDTO {
    private String request_id;
    private String requester_id;
    private String addressee_id;
    private String status;
    private String other_user_id;
    private String other_user_username;
}