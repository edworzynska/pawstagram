package com.example.instargam.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="Posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    public void setDescription(String description){
        if (description.length() > 1000){
            throw new InvalidParameterException("Text is too long: maximum number of characters is 1000.");
        }
        this.description = description;
    }
    public void setImgUrl(String imgUrl){
        if (imgUrl.isEmpty() || imgUrl.isBlank()){
            throw new InvalidParameterException("Error while adding the photo to the post.");
        }
        this.imgUrl = imgUrl;
    }

    public Post() {
    }

    public Post(Long id, User user, String imgUrl, String description) {
        this.id = id;
        this.user = user;
        this.imgUrl = imgUrl;
        this.description = description;
    }
}
