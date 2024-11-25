package com.example.instargam.service;


import com.example.instargam.dto.UserDTO;
import com.example.instargam.model.Like;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {

    private LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional
    public void likePost(Post post, User user){
        if (!likeRepository.existsByPostAndUser(post, user)){
            Like like = new Like(post, user);
            likeRepository.save(like);
        }
    }
    @Transactional
    public void unlikePost(Post post, User user){
        if (likeRepository.existsByPostAndUser(post, user)){
            likeRepository.deleteByPostAndUser(post, user);
        }
    }
    public Long getNumberOfLikes(Post post){
        return likeRepository.countByPost(post);
    }

    public List<UserDTO> getPostLikes(Post post){

        List<Like> likes = likeRepository.findByPost(post);

        return likes.stream().map(like -> new UserDTO(
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getUser().getProfileImgUrl(),
                "/users/" + like.getUser().getUsername())).toList();
    }
}
