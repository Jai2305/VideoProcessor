package com.example.videoEditor;

import com.example.videoEditor.utils.VideoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VideoHelperTest {

    @Test
    public void testCreateDirectoryIfNotExists_NewDirectory() throws IOException {
        String newDirPath = "test_dir";
        File createdDir = VideoHelper.createDirectoryIfNotExists(newDirPath);
        assertTrue(createdDir.isDirectory());
        assertTrue(createdDir.exists());
        Files.deleteIfExists(Paths.get(newDirPath));
    }

    @Test
    public void testCreateDirectoryIfNotExists_ExistingDirectory() throws IOException {
        String existingDirPath = "existing_dir";
        File existingDir = new File(existingDirPath);
        File createdDir = VideoHelper.createDirectoryIfNotExists(existingDirPath);
        assertTrue(existingDir.isDirectory());
        assertTrue(existingDir.exists());
        assertTrue(existingDir.getAbsolutePath().equals(createdDir.getAbsolutePath())); // Check if the same directory object is returned
        Files.deleteIfExists(Paths.get(existingDirPath));
    }

    @Test
    public void testCreateDirectoryIfNotExists_Output() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String directoryPath = "test_dir";
        VideoHelper.createDirectoryIfNotExists(directoryPath);

        String output = outputStream.toString().trim();

        assertTrue(output.contains("Directory created successfully:"));
        assertFalse(output.contains("Failed to create directory:"));
        assertFalse(output.contains("Directory already exists:"));

        Files.deleteIfExists(Paths.get(directoryPath));
        System.setOut(System.out);
    }
}
