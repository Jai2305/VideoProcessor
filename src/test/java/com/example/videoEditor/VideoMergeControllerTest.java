package com.example.videoEditor;

import com.example.videoEditor.controllers.VideoMergeController;
import com.example.videoEditor.utils.FfmpegHelper;
import com.example.videoEditor.utils.VideoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VideoMergeControllerTest {
    @Test
    public void testMergeVideos_Success() throws IOException, InterruptedException {
        // Mock multiple MultipartFile objects representing video clips
        List<MultipartFile> mockClips = createMockMultipartFileList(2);

        // Call the controller method
        ResponseEntity<?> response = new VideoMergeController().mergeVideos(mockClips);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.parseMediaType("video/mp4")));
    }

    private List<MultipartFile> createMockMultipartFileList(int numFiles) throws IOException {
        List<MultipartFile> mockFiles = new ArrayList<>();
        for (int i = 0; i < numFiles; i++) {
            String filename = "clip" + (i + 1) + ".mp4";
            String contentType = "video/mp4";
            byte[] bytes = "This is a mock clip content".getBytes();
            mockFiles.add(new MockMultipartFile(filename, filename, contentType, bytes));
        }
        return mockFiles;
    }
    @Test
    public void testMergeVideos_EmptyClipList() throws IOException {
        // Empty list of clips
        List<MultipartFile> emptyClips = Collections.emptyList();

        // Call the controller method
        ResponseEntity<?> response = new VideoMergeController().mergeVideos(emptyClips);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void testCleanUpTemporaryFiles_Success() throws IOException {
        // Create temporary files (directory, list file, clips)
        File tempDir = Files.createTempDirectory("test_merge").toFile();
        File listFile = new File(tempDir, "clips.txt");
        List<MultipartFile> mockClips = createMockMultipartFileList(2);
        for (MultipartFile clip : mockClips) {
            File clipFile = new File(tempDir, clip.getOriginalFilename());
            clip.transferTo(clipFile);
        }

        // Call the cleanup method
        VideoHelper.cleanUpTemporaryFiles(tempDir.getAbsolutePath(), listFile, mockClips);

        // Assertions
        assertTrue(tempDir.delete()); // Ensure directory is empty before deleting
        assertFalse(tempDir.exists());
        assertFalse(listFile.exists());
        for (MultipartFile clip : mockClips) {
            assertFalse(new File(tempDir, clip.getOriginalFilename()).exists());
        }
    }
    @Test
    public void testGetMergeCommand_CorrectCommand() {
        String clipListPath = "/tmp/clips.txt";
        String outputPath = "/tmp/merged_output.mp4";
        String[] expectedCommand = {"ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", clipListPath, "-c", "copy", outputPath};

        String[] actualCommand = FfmpegHelper.getMergeCommand(clipListPath, outputPath);

        assertArrayEquals(expectedCommand, actualCommand);
    }

}
