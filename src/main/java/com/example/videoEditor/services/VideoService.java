package com.example.videoEditor.services;

import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.VideoRepository;
import com.example.videoEditor.utils.FfmpegHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class VideoService {
    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }



    public Video saveVideo(String filename, String desc, String filePath) {
        Video video = new Video();
        video.setTitle(filename);
        video.setDescription(desc);
        video.setFilePath(filePath);
        videoRepository.save(video);
        return video;
    }

    public Video findVideo(Long videoId) {
        return videoRepository.findById(videoId).orElse(null);
    }

    public String validate(Long startTime, Long endTime, String filePath) {
        long videoDuration;
        try {
            videoDuration = FfmpegHelper.getVideoDuration(filePath);
        } catch (InterruptedException | IOException e) {
            return "Validation failed: "+ Arrays.toString(e.getStackTrace());
        }
        // Validate start and end times
        if (startTime < 0 || endTime < 0 || startTime >= endTime) {
            return "Invalid start or end time";
        }
        if (endTime > videoDuration) {
            return "End time cannot be greater than video duration";
        }
        return "success";
    }
}
