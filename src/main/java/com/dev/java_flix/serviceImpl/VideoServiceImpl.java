package com.dev.java_flix.serviceImpl;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dao.VideoRepository;
import com.dev.java_flix.dto.request.VideoRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.VideoResponse;
import com.dev.java_flix.dto.response.VideoStatsResponse;
import com.dev.java_flix.entity.Video;
import com.dev.java_flix.service.VideoService;
import com.dev.java_flix.util.PaginationUtils;
import com.dev.java_flix.util.ServiceUtils;

@Service
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ServiceUtils serviceUtils;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository, ServiceUtils serviceUtils){
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public MessageResponse createVideoByAdmin(VideoRequest videoRequest) {
        //Pendencia: validar se existe os UUIDs em Uploads
        
        Video video = new Video();

        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setYear(videoRequest.getYear());
        video.setRating(videoRequest.getRating());
        video.setDuration(videoRequest.getDuration());
        video.setSrcUuid(videoRequest.getSrc());
        video.setPosterUuid(videoRequest.getPoster());
        video.setPublished(videoRequest.isPublished());
        video.setCategories(videoRequest.getCategories() != null ? videoRequest.getCategories() : List.of());

        videoRepository.save(video);

        return new MessageResponse("Video created successfully.");
    }

    @Override
    public PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search) {
        
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<Video> videoPage;

        if (search != null && !search.trim().isEmpty()){
            videoPage = videoRepository.searchVideos(search.trim(), pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }
        
        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
    }

    @Override
    public MessageResponse updateVideoByAdmin(Long id, VideoRequest videoRequest) {
        Video video = new Video();
        video.setId(id);
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setYear(videoRequest.getYear());
        video.setRating(videoRequest.getRating());
        video.setDuration(videoRequest.getDuration());
        video.setSrcUuid(videoRequest.getSrc());
        video.setPosterUuid(videoRequest.getPoster());
        video.setCategories(videoRequest.getCategories() != null ? videoRequest.getCategories() : List.of());

        videoRepository.save(video);

        return new MessageResponse("Video updated successfully.");
    }

    @Override
    public MessageResponse deleteVideoByAdmin(Long id) {
        
        if(!videoRepository.existsById(id)){
            throw new IllegalArgumentException("Video not found: " + id);
        }

        videoRepository.deleteById(id);
        return new MessageResponse("Video deleted successfully.");
    }

    @Override
    public MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean status) {
        
        Video video = serviceUtils.getVideoByIdOrThrow(id);
        video.setPublished(status);

        videoRepository.save(video);
        return new MessageResponse("Video publish status updated successfuly.");
    }

    @Override
    public VideoStatsResponse getAdminStats() {
        
        long totalVideo = videoRepository.count();
        long publishedVideos = videoRepository.countPublishedVideos();
        long totalDuration = videoRepository.getTotalDuration();

        return new VideoStatsResponse(totalVideo, publishedVideos, totalDuration);
    }

    @Override
    public PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email) {
        
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<Video> videoPage;

        if (search != null && !search.trim().isEmpty()){
            videoPage = videoRepository.searchPublishedVideos(search.trim(), pageable);
        } else {
            videoPage = videoRepository.findPublishedVideos(pageable);
        }

        List<Video> videos = videoPage.getContent();

        Set<Long> watchListIds = Set.of();

        if (!videos.isEmpty()){
            try {
                List<Long> videosIds = videos.stream().map(Video::getId).toList();
                watchListIds = userRepository.findWatchListVideosIds(email, videosIds);
            } catch (Exception e) {
                watchListIds = Set.of();
            }
        }

        Set<Long> finalWatchListIds = watchListIds;
        videos.forEach(video -> video.setIsInWatchlist(finalWatchListIds.contains(video.getId())));

        List<VideoResponse> videoResponses = videos.stream().map(VideoResponse::fromEntity).toList();

        return PaginationUtils.toPageResponse(videoPage, videoResponses);
    }

    @Override
    public List<VideoResponse> getFeaturedVideos() {
        
        Pageable pageable = PageRequest.of(0, 5);
        List<Video> videos = videoRepository.findRandomPublishedVideos(pageable);

        return videos.stream().map(VideoResponse::fromEntity).toList();
    }

}
