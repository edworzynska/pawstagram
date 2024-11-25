package com.example.instargam.service;

import com.example.instargam.dto.UserDTO;
import com.example.instargam.model.Follow;
import com.example.instargam.model.User;
import com.example.instargam.repository.FollowRepository;
import com.example.instargam.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {

    private FollowRepository followRepository;
    private UserRepository userRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void followUser(User follower, String followingUsername){
        User following = userRepository.findByUsername(followingUsername).orElseThrow(()->
                new EntityNotFoundException("User doesn't exist!"));

        if (follower.equals(following)){
            throw new IllegalArgumentException("Unable to follow owned account.");
        }
        if (!followRepository.existsByFollowerAndFollowing(follower, following)){
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);

            followRepository.save(follow);
        }
    }
    @Transactional
    public void unfollowUser(User follower, String followingUsername){

        User following = userRepository.findByUsername(followingUsername).orElseThrow(()->
                new EntityNotFoundException("User doesn't exist!"));

       if (followRepository.existsByFollowerAndFollowing(follower, following)){
            followRepository.deleteByFollowerAndFollowing(follower, following);
        }
    }

    public List<UserDTO> getFollowers(String followingUsername){

        User following = userRepository.findByUsername(followingUsername).orElseThrow(()->
                new EntityNotFoundException("User doesn't exist!"));

        List<Follow> follows = followRepository.findByFollowing(following);

        return follows.stream().map(follow -> new UserDTO(
                follow.getFollower().getId(),
                follow.getFollower().getUsername(),
                follow.getFollower().getProfileImgUrl(),
                "/users/" + follow.getFollower().getUsername()))
                .toList();
    }
    public List<UserDTO> getFollowing(String followerUsername){
        User follower = userRepository.findByUsername(followerUsername).orElseThrow(()->
                new EntityNotFoundException("User doesn't exist!"));

        List<Follow> follows = followRepository.findByFollower(follower);

        return follows.stream().map(follow -> new UserDTO(
                        follow.getFollowing().getId(),
                        follow.getFollowing().getUsername(),
                        follow.getFollowing().getProfileImgUrl(),
                        "/users/" + follow.getFollowing().getUsername()))
                .toList();
    }
    public Long getNumberOfFollowers(User following){
        return followRepository.countByFollowing(following);
    }
    public Long getNumberOfFollowing(User follower){
        return followRepository.countByFollower(follower);
    }
}
