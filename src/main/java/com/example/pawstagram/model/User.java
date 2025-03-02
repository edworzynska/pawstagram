package com.example.pawstagram.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "bio")
    private String bio;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    public void setBio(String bio){
        if (bio.isBlank() || bio.isEmpty()){
            throw new InvalidParameterException("Bio cannot be empty!");
        }
        if (bio.length() > 150){
            throw new InvalidParameterException("Invalid length! Maximum number of characters: 150");
        }
        this.bio = bio;
    }

    public User() {
    }

    public User(Long id, String name, String email, String password, String bio) {
        this.id = id;
        this.username = name;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }
}
