package com.example.videoEditor;

import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.VideoRepository;
import com.example.videoEditor.services.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VideoServiceTest {
    @Test
    public void testSaveVideo_Success() {
        String filename = "test.mp4";
        String desc = "This is a test video";
        String filePath = "/path/to/video.mp4";

        VideoService videoService = new VideoService(Mockito.mock(VideoRepository.class)); // Mock repository

        Video savedVideo = videoService.saveVideo(filename, desc, filePath);

        assertNotNull(savedVideo);
        assertEquals(filename, savedVideo.getTitle());
        assertEquals(desc, savedVideo.getDescription());
        assertEquals(filePath, savedVideo.getFilePath());
    }

    @Test
    public void testFindVideo_Success() {
        Long videoId = 1L;
        Video mockVideo = new Video(videoId, "test.mp4", "Desc", "path/to/video.mp4");

        VideoRepository mockRepository = Mockito.mock(VideoRepository.class);
        Mockito.when(mockRepository.findById(videoId)).thenReturn(Optional.of(mockVideo));

        VideoService videoService = new VideoService(mockRepository);

        Video foundVideo = videoService.findVideo(videoId);

        assertNotNull(foundVideo);
        assertEquals(mockVideo, foundVideo);
    }

    @Test
    public void testFindVideo_NotFound() {
        Long videoId = 1L;

        VideoRepository mockRepository = Mockito.mock(VideoRepository.class);
        Mockito.when(mockRepository.findById(videoId)).thenReturn(Optional.empty());

        VideoService videoService = new VideoService(mockRepository);

        Video foundVideo = videoService.findVideo(videoId);

        assertNull(foundVideo);
    }

    @Test
    public void testValidate_InvalidStartEndTime() {
        Long startTime = -10000L; // Negative start time
        Long endTime = 20000L;
        String filePath = "/path/to/video.mp4";

        VideoService videoService = new VideoService(Mockito.mock(VideoRepository.class));

        String validationResult = videoService.validate(startTime, endTime, filePath);

        assertEquals("Invalid start or end time", validationResult);
    }

}
