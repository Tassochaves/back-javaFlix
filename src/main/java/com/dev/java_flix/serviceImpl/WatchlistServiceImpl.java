package com.dev.java_flix.serviceImpl;

import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.entity.User;
import com.dev.java_flix.entity.Video;
import com.dev.java_flix.service.WatchlistService;
import com.dev.java_flix.util.ServiceUtils;

@Service
public class WatchlistServiceImpl implements WatchlistService{

    private final UserRepository userRepository;
    private final ServiceUtils serviceUtils;

    public WatchlistServiceImpl(UserRepository userRepository, ServiceUtils serviceUtils){
        this.userRepository = userRepository;
        this.serviceUtils = serviceUtils;
    }
    
    @Override
    public MessageResponse addToWatchlist(String email, Long videoId) {
        
        User user = serviceUtils.getUserByEmailOrThrow(email);
        Video video = serviceUtils.getVideoByIdOrThrow(videoId);

        user.addToWatchlist(video);
        userRepository.save(user);

        return new MessageResponse("Video added to watchlist successfully.");
    }

}
