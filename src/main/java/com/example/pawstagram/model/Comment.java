package com.example.pawstagram.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "contents", nullable = false, length = 2000)
    private String contents;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    public Comment() {
    }

    public Comment(Post post, User user, String contents) {
        this.post = post;
        this.user = user;
        this.contents = contents;
    }

    public void setContents(String contents) {
        if (contents.length() > 2000){
            throw new InvalidParameterException("Unable to post the comment; maximum number of characters is 2000!");
        }
        if (contents.isBlank() || contents.isEmpty()){
            throw new InvalidParameterException("The comment cannot be empty!");
        }
        this.contents = contents;
    }
}
