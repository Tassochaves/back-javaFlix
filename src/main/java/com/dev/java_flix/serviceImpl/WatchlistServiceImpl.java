package com.dev.java_flix.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.VideoResponse;
import com.dev.java_flix.entity.User;
import com.dev.java_flix.entity.Video;
import com.dev.java_flix.service.WatchlistService;
import com.dev.java_flix.util.PaginationUtils;
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

    @Override
    public MessageResponse removeFromWatchlist(String email, Long videoId) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        Video video = serviceUtils.getVideoByIdOrThrow(videoId);

        user.removeFromWatchlist(video);
        userRepository.save(user);

        return new MessageResponse("Video removed from watchlist successfully.");
    }

    @Override
    public PageResponse<VideoResponse> getWatchlistPaginated(String email, int page, int size, String search) {
        User user = serviceUtils.getUserByEmailOrThrow(email);

        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Video> videoPage;

        if (search!=null && !search.trim().isEmpty()) {
            videoPage = userRepository.searchWatchlistByUserId(user.getId(), search.trim(), pageable);
        } else {
            videoPage = userRepository.findWatchListByUserId(user.getId(), pageable);
        }

        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);

    }

}
