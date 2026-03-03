package com.dev.java_flix.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.VideoRepository;
import com.dev.java_flix.dto.request.VideoRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.entity.Video;
import com.dev.java_flix.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
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

}
