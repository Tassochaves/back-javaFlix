package com.dev.java_flix.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "videos")
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    private Integer year;
    private String rating;
    private Integer duration;

    @JsonIgnore
    @Column(name = "src") 
    private String srcUuid;

    @JsonIgnore
    @Column(name = "poster") 
    private String posterUuid;

    @Column(nullable = false)
    private boolean published = false;

    @Transient
    @JsonProperty("isInWatchlist")
    private Boolean isInWatchlist;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_categories", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "category")
    private List<String> categories = new ArrayList<>();


    // Gera dinamicamente a URL completa do vídeo, identifica automaticamente o domínio, expoe o campo "src" no JSON de saída.
    @JsonProperty("src")
    public String getSrc(){

        if(srcUuid != null && !srcUuid.isEmpty()){
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toString();
            return baseUrl + "/api/files/video/" +srcUuid;
        }

        return null;
    }

    // Gera a URL completa da imagem de capa (poster) para o frontend, usando o UUID armazenado no banco e detectando automaticamente o endereço do servidor.
    @JsonProperty("poster")
    public String getPoster(){

        if(posterUuid != null && !posterUuid.isEmpty()){
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toString();
            return baseUrl + "/api/files/image/" +posterUuid;
        }

        return null;
    }
}
