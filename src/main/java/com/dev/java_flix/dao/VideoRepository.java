package com.dev.java_flix.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.java_flix.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long>{

}
