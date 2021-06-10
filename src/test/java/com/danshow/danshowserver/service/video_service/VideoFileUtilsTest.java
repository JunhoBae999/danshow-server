package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.FileRepository;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VideoFileUtilsTest {

    @Autowired
    VideoFileUtils videoFileUtils;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    S3Uploader s3Uploader;

    @Test
    void extractAudio() throws IOException {
        String inputPath = System.getProperty("user.dir") + "/files/woman/fire.mp4";
        String originalFileName = "fire.mp4";
        String outputPath = System.getProperty("user.dir") + "/files/video";
        String path = videoFileUtils.extractAudio(inputPath,originalFileName, outputPath);
        System.out.println("path = " + path);
    }

    @Test
    void getS3FileAndSaveToLocalTest() throws IOException {

        AttachFile attachFile =
                fileRepository.findById(4L).orElseThrow(NoSuchElementException::new);

        byte[] bytes = s3Uploader.getObject(attachFile);
        String temporalFilePath = System.getProperty("user.dir") + "/tmp/"+attachFile.getOriginalFileName();
        videoFileUtils.writeToFile(temporalFilePath, bytes);
    }

    @Test
    void getMemberTestVideoUploadedToS3() throws  IOException {
    }

    @Test
    void resizeTest() throws IOException {
        String inputPath = System.getProperty("user.dir") + "/files/";
        String originalFileName = "test";
        String path = videoFileUtils.resizeFile(inputPath,originalFileName);
    }

    @Test
    void integrateMp3Test() throws IOException {

        String videotPath = System.getProperty("user.dir") + "/tmp/test.mp4";
        String audioPath = System.getProperty("user.dir") + "/tmp/taudio.mp3";
        String outputPath = System.getProperty("user.dir") + "/tmp/test_audio_integrated.mp4";

        videoFileUtils.integrateAudio(videotPath,audioPath,outputPath);

    }

}