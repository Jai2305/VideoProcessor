package com.example.videoEditor.repositories;

import com.example.videoEditor.entitites.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long>{

}
