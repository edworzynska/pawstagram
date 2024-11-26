package com.example.instargam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {

    private Long id;
    private String username;
    private String imgUrl;
    private String description;
    private LocalDateTime date;
    private Long likesCount;
    private Long commentsCount;

    public PostDTO(Long id, String username, String imgUrl, String description, Long commentsCount, Long likesCount, LocalDateTime date) {
        this.id = id;
        this.username = username;
        this.imgUrl = imgUrl;
        this.description = description;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
        this.date = date;



    }

    public PostDTO() {
    }
}
