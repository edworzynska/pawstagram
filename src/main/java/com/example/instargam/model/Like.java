package com.example.instargam.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="Likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id"})})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    public Like() {
    }

    public Like(Long id, Post post, User user) {
        this.id = id;
        this.post = post;
        this.user = user;
    }

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
