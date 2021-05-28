package com.danshow.danshowserver.service.video_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VideoFileUtilsTest {

    @Autowired
    VideoFileUtils videoFileUtils;

    @Test
    void extractAudio() throws IOException {
        String inputPath = System.getProperty("user.dir") + "/files/woman/fire.mp4";
        String originalFileName = "fire.mp4";
        String outputPath = System.getProperty("user.dir") + "/files/video";
        String path = videoFileUtils.extractAudio(inputPath,originalFileName, outputPath);
        System.out.println("path = " + path);
    }
}