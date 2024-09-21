package com.example.videoEditor;

import com.example.videoEditor.utils.FfmpegHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FfmpegHelperTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private FfmpegHelper ffmpegHelper;

    @BeforeEach
    void setUp() {
        ffmpegHelper = new FfmpegHelper();
        System.setOut(new PrintStream(outContent)); // Redirect System.out to capture output
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut); // Restore original System.out after each test
    }

    // Test for getTrimCommand
    @Test
    void testGetTrimCommand() {
        String inputFilePath = "input.mp4";
        Long startTime = 0L;
        Long endTime = 10L;
        String outputFilePath = "output.mp4";

        String[] expectedCommand = {
                "ffmpeg", "-y", "-i", inputFilePath, "-ss", "0", "-to", "10", "-c", "copy", outputFilePath
        };

        String[] actualCommand = FfmpegHelper.getTrimCommand(inputFilePath, startTime, endTime, outputFilePath);

        assertArrayEquals(expectedCommand, actualCommand);
    }

    // Test for getDurationCommand
    @Test
    void testGetDurationCommand() {
        String videoFilePath = "input.mp4";

        String[] expectedCommand = {
                "ffmpeg", "-i", videoFilePath
        };

        String[] actualCommand = FfmpegHelper.getDurationCommand(videoFilePath);

        assertArrayEquals(expectedCommand, actualCommand);
    }

    // Test runCommand
    @Test
    void testLogOutputForInputStream() throws IOException {
        // Simulate InputStream with some test data
        String inputString = "Line1\nLine2\nLine3\n";
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());

        // Capture system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        FfmpegHelper.logOutputForInputStream(inputStream);

        // Build the expected output using system's line separator
        String expectedOutput = "Line1" + System.lineSeparator() +
                "Line2" + System.lineSeparator() +
                "Line3" + System.lineSeparator();

        // Verify that the output matches the expected output
        assertEquals(expectedOutput, outContent.toString());
    }

}

