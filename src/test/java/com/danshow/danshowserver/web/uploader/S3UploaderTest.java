package com.danshow.danshowserver.web.uploader;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback(value = false)
class S3UploaderTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private S3Uploader s3Uploader;

    @Test
    public void multipartFileUploadTest() throws IOException {
        MockMultipartFile video = new MockMultipartFile("video_s3",
                "video_s3.mp4",
                "video/mp4",
                "video test".getBytes(StandardCharsets.UTF_8));

        String result = s3Uploader.upload(video,"image");
        System.out.println("result_multipart = " + result);
    }

    @Test
    public void localFileUploadTest() throws IOException {
        String result = s3Uploader
                .upload("files/woman/woman_thumbnail.gif","test.gif","image");
        System.out.println("result2_filename = " + result);
    }
}