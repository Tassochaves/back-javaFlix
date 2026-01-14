package com.dev.java_flix.util;

import org.springframework.stereotype.Component;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dao.VideoRepository;
import com.dev.java_flix.entity.User;
import com.dev.java_flix.entity.Video;
import com.dev.java_flix.exception.ResourceNotFoundException;

@Component
public class ServiceUtils {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public ServiceUtils(UserRepository userRepository, VideoRepository videoRepository){
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public User getUserByEmailOrThrow(String email){
        return userRepository.findByEmail(email)
                    .orElseThrow( () -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getUserByIdOrThrow(Long id){
        return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Video getVideoByIdOrThrow(Long id){
        return videoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }
}
