package com.example.instargam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private String username;
    private String userProfileImg;
    private String contents;
    private LocalDateTime date;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String username, String userProfileImg, String contents, LocalDateTime date) {
        this.id = id;
        this.username = username;
        this.userProfileImg = userProfileImg;
        this.contents = contents;
        this.date = date;
    }
}
