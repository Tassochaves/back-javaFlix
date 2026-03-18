package com.dev.java_flix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.java_flix.dto.request.VideoRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.VideoResponse;
import com.dev.java_flix.dto.response.VideoStatsResponse;
import com.dev.java_flix.service.VideoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService){
        this.videoService = videoService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MessageResponse> createVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest){
        return ResponseEntity.ok(videoService.createVideoByAdmin(videoRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<VideoResponse>> getAllAdminVideos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ){
        return ResponseEntity.ok(videoService.getAllAdminVideos(page, size, search));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}") 
    public ResponseEntity<MessageResponse> updateVideoByAdmin(
        @PathVariable Long id, 
        @Valid @RequestBody VideoRequest videoRequest){

            return ResponseEntity.ok(videoService.updateVideoByAdmin(id, videoRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}") 
    public ResponseEntity<MessageResponse> deleteVideoByAdmin(@PathVariable Long id){
        return ResponseEntity.ok(videoService.deleteVideoByAdmin(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/publish") 
    public ResponseEntity<MessageResponse> toggleVideoPublishStatusByAdmin(
        @PathVariable Long id,
        @RequestParam boolean value){

            return ResponseEntity.ok(videoService.toggleVideoPublishStatusByAdmin(id, value));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats") 
    public ResponseEntity<VideoStatsResponse> getAdminStats(){
        return ResponseEntity.ok(videoService.getAdminStats());
    }

    @GetMapping("/published")
    public ResponseEntity<PageResponse<VideoResponse>> getPublishedVideos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        Authentication authentication
    ) {
        String email = authentication.getName();
        PageResponse<VideoResponse> response = videoService.getPublishedVideos(page, size, search, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<VideoResponse>> getFeaturedVideos(){
        List<VideoResponse> response = videoService.getFeaturedVideos();
        return ResponseEntity.ok(response);
    }
}
