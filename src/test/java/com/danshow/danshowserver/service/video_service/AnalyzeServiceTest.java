package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.web.dto.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnalyzeServiceTest {

    @Autowired
    private AnalyzeService analyzeService;

    @Autowired
    private VideoFileUtils videoFileUtils;


    @Test
    public void testSplitingVideo() throws IOException {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        File file = realVideo.getFile();

        List<String> returnLocations = new ArrayList<>();

        analyzeService.splitVideoIntoTwo(returnLocations, file, 2);

        for (String returnLocation : returnLocations) {
            Assertions.assertThat(returnLocation).isNotNull();
            System.out.println("result : " + returnLocation);
        }
    }

    @Test
    public void testIntegrateFile() throws IOException {

        File fileA = new File((System.getProperty("user.dir") + "/files/woman/01_woman.mp4"));
        File fileB = new File((System.getProperty("user.dir") + "/files/woman/02_woman.mp4"));

        Response firstResource = new Response(1, fileA);
        Response secondResource = new Response(2, fileB);

        analyzeService.integrateFile(firstResource,secondResource);
    }

    @Test
    public void splitVideoWithFFmpeg() throws IOException {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        String outputPath = System.getProperty("user.dir") + "/files";
        videoFileUtils.splitFile(realVideo.getFile().getAbsolutePath(),realVideo.getFile().getName(),outputPath,2);
    }

    @Test
    public void IntegrateFilewWithFFmpeg() throws  IOException {

        String inputPath = System.getProperty("user.dir") + "/files/woman";
        String oriName = "woman";

        videoFileUtils.integrateFiles(inputPath,oriName);

    }

    @Test
    public void createTxtFileTest() throws Exception {

        String totalPath = System.getProperty("user.dir") + "/files/woman/woman_1.mp4";
        String fileJoinPath = System.getProperty("user.dir") + "/files/woman";
        String oriName = "woman";

        videoFileUtils.createTxt(totalPath,fileJoinPath,oriName);

        String totalPathSecond = System.getProperty("user.dir") + "/files/woman/woman_2.mp4";

        videoFileUtils.createTxt(totalPathSecond,fileJoinPath,oriName);
    }

    @Test
    public void extractThumbnail() throws Exception {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        String outputPath = System.getProperty("user.dir") + "/files";
        videoFileUtils.extractThumbnail(realVideo.getFile().getAbsolutePath(),realVideo.getFile().getName(),outputPath);
    }


    @Test
    public void testIntegrateSideBySide() throws  Exception {
        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");
        String outputPath = System.getProperty("user.dir") + "/files/";
        videoFileUtils.integrateFileSideBySide(realVideo.getFile().getAbsolutePath(), realVideo.getFile().getAbsolutePath(),outputPath);
    }




}