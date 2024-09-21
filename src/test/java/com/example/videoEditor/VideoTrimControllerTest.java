package com.example.videoEditor;

import com.example.videoEditor.controllers.VideoTrimController;
import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.VideoRepository;
import com.example.videoEditor.services.VideoService;
import com.example.videoEditor.utils.FfmpegHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VideoTrimControllerTest {

    @Autowired
    private VideoRepository videoRepository;

    private static VideoService videoService;

    @BeforeAll
    public static void setUp(){
        videoService = Mockito.mock(VideoService.class);
    }


    @Test
    public void testGetTrimmedVideo_Success() throws IOException, InterruptedException {

        Video mockVideo = new Video(1L, "test.mp4", "Desc", "path/to/video.mp4");
        Long videoId = 1L;
        Long startTime = 10000L; // 10 seconds
        Long endTime = 20000L; // 20 seconds
        Mockito.when(videoService.findVideo(1L)).thenReturn(mockVideo);
        Mockito.when(videoService.validate(anyLong(), anyLong(), anyString())).thenReturn("success");
        ResponseEntity<?> response = new VideoTrimController(videoService).getTrimmedVideo(videoId, startTime, endTime);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.parseMediaType("video/mp4")));
    }
    @Test
    public void testGetTrimmedVideo_VideoNotFound() throws IOException, InterruptedException {
        // Mock VideoService to return null
        Mockito.when(videoService.findVideo(1L)).thenReturn(null);

        // Call the controller method
        Long videoId = 1L;
        Long startTime = 10000L;
        Long endTime = 20000L;

        ResponseEntity<?> response = new VideoTrimController(videoService).getTrimmedVideo(videoId, startTime, endTime);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testGetTrimmedVideo_InvalidTrimRequest() throws IOException, InterruptedException {
        // Mock VideoService to return validation error
        Video mockVideo = new Video(1L, "test.mp4", "Desc", "path/to/video.mp4");
        Mockito.when(videoService.findVideo(1L)).thenReturn(mockVideo);
        Mockito.when(videoService.validate(anyLong(), anyLong(), anyString())).thenReturn("Invalid trim request");  // Simulate validation error

        // Call the controller method
        Long videoId = 1L;
        Long startTime = 30000L; // 30 seconds (invalid: after video length)
        Long endTime = 20000L; // 20 seconds

        ResponseEntity<?> response = new VideoTrimController(videoService).getTrimmedVideo(videoId, startTime, endTime);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid trim request"));  // Check for the validation error message in the response body
    }

}
