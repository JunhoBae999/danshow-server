package com.danshow.danshowserver.service.video_service;

import antlr.Token;
import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.Role;
import com.danshow.danshowserver.web.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@Slf4j
class AnalyzeServiceTest {

    @Autowired
    private AnalyzeService analyzeService;

    @Autowired
    private VideoFileUtils videoFileUtils;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenProvider tokenProvider;

    ExchangeStrategies exchangeStrategies =
            ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                    .build();// to unlimited memory size .build();

    WebClient webClient = WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .build();

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

        File fileA = new File((System.getProperty("user.dir") + "/files/dancer.mp4"));
        File fileB = new File((System.getProperty("user.dir") + "/files/test.mp4"));

        Response firstResource = new Response(1, fileA);
        Response secondResource = new Response(2, fileB);

        analyzeService.integrateFile(firstResource,secondResource);
    }

    @Test
    public void splitVideoWithFFmpeg() throws IOException {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/mergedvid.mp4");

        String outputPath = System.getProperty("user.dir") + "/files";
        videoFileUtils.splitFile(realVideo.getFile().getAbsolutePath(),realVideo.getFile().getName(),outputPath,3);
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
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/real_test.mp4");

        String inpuPath = System.getProperty("user.dir")+"/src/main/resources/demofile/";
        String originalFileName = "real_test";

        String filePath = videoFileUtils.resizeFile(inpuPath,originalFileName);

        Resource realVideo2 = defaultResourceLoader.getResource("classpath:demofile/dancer.mp4");

        String outputPath = System.getProperty("user.dir") + "/files/side_by_side_test.mp4";
        videoFileUtils.integrateFileSideBySide(filePath, realVideo2.getFile().getAbsolutePath(),outputPath);
    }

    @Test
    void testMemberVideoTest() throws IOException {

        //given
        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("member_test_video","member_test_video.mp4","video/mp4",
                Files.readAllBytes(Path.of(realVideo.getFile().getAbsolutePath())));

        //mocking needed.
    }

    @Test
    void sendVideoAsyncTest() throws Exception {

        Member member = Member.builder()
                .membership(true)
                .email("testt.test@test.test")
                .nickname("tester")
                .name("testjunho")
                .role(Role.MEMBER)
                .build();

        memberRepository.save(member);

        String token = tokenProvider.createToken(member.getEmail(), Role.MEMBER.getKey());

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        final String EXAMPLE = "http://1e6c9fcd9326.ngrok.io/one";

    }

}