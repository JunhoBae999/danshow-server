package com.danshow.danshowserver.service.video_service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class VideoServiceReleaseTest {

    @Autowired
    private VideoServiceRelease videoService;

    @Test
    public void splitTest() throws Exception {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");
        MockMultipartFile video = new MockMultipartFile("video",
                "video.mp4",
                "video/mp4",
                Files.readAllBytes(realVideo.getFile().toPath()));

        List<String> splitFileList = videoService.split(video,2);
        assertThat(splitFileList.size()).isEqualTo(2);
        for(String s : splitFileList) {
            System.out.println("s = " + s);
        }
    }

}