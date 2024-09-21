package com.example.videoEditor;

import com.example.videoEditor.controllers.VideoController;
import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.VideoRepository;
import com.example.videoEditor.services.VideoService;
import com.example.videoEditor.utils.VideoHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VideoControllerTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    public void testUploadVideo_Success() throws IOException {
        // Mock a valid multipart file
        MultipartFile mockFile = createMockMultipartFile("test.mp4", "video/mp4");

        // Mock VideoService behavior by saving a dummy Video object
        videoRepository.save(new Video(1L, "test.mp4", "Desc", "..."));

        // Call the controller method
        ResponseEntity<?> response = new VideoController(videoService).uploadVideo(mockFile);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Video uploaded successfully", response.getBody());
    }

    private static MultipartFile createMockMultipartFile(String filename, String contentType) throws IOException {
        byte[] bytes = "This is a mock file content".getBytes();
        return new MockMultipartFile(filename, filename, contentType, bytes);
    }

    @Test
    public void testGetVideo_Success() throws IOException {
        // Mock VideoService behavior by saving a Video object
        Video mockVideo = new Video(1L, "test.mp4", "Desc", "path/to/video.mp4");
        videoRepository.save(mockVideo);

        // Call the controller method
        ResponseEntity<Resource> response = new VideoController(videoService).getVideo(1L);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.parseMediaType(VideoHelper.MEDIA_TYPE_MP4)));
    }
}