package com.example.instargam.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String profileImgUrl;
    private String profileUrl;

    public UserDTO(Long id, String username, String profileImgUrl, String profileUrl) {
        this.id = id;
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.profileUrl = profileUrl;
    }

    public UserDTO() {
    }
}
