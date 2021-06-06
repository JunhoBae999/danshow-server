package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DancerRepository dancerRepository;
    @Autowired
    VideoPostRepository videoPostRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TokenProvider tokenProvider;

    @BeforeEach
    public  void setUp() {

        Member member = Member.builder()
                .membership(true)
                .email("test.test1@test.test")
                .nickname("tester")
                .name("testjunho")
                .role(Role.MEMBER)
                .build();

        memberRepository.save(member);

        Dancer dancer = Dancer.builder()
                .dancer_description("test dancer desc")
                .email("dancer_test1@test.test")
                .nickname("testdencer")
                .name("testerdancer")
                .role(Role.DANCER)
                .build();

        dancerRepository.save(dancer);

    }


    @Test
    @DisplayName("안무가가 영상과 포스팅을 함께 올리는 경우 테스")
    public void videoUploadTest() throws Exception {


        MockMultipartFile video = new MockMultipartFile("video",
                "video.mp4",
                "video/mp4",
                "video test".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile image = new MockMultipartFile("thumbnail",
                "image.jpeg",
                "image/jpeg",
                "image test".getBytes(StandardCharsets.UTF_8));

        String videoPostContent = objectMapper.writeValueAsString(new VideoPostSaveDto("test title", "test description", "test.test@test.test",
                1L, "idol", "boy", 1L));

        MockMultipartFile json = new MockMultipartFile("post", "video_post", "application/json", videoPostContent.getBytes(StandardCharsets.UTF_8));

        MvcResult result = this.mockMvc.perform(multipart("/api/v1/file")
                .file(video)
                .file(image)
                .file(json)
                .param("userID", "test.test@test.test")
                .contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String output = result.getResponse().getContentAsString();

        System.out.println("=====resonse content======");
        System.out.println(output);
    }


    @Test
    @DisplayName("로컬의 demofile 폴더의 woman, fuck 파일을 전송하여 filespath 폴더로 옮기고 디비에 저장한다.")
    public void testRealUpload() throws Exception {

        final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

        Resource realVideo = defaultResourceLoader.getResource("classpath:demofile/woman.mp4");

        MockMultipartFile video = new MockMultipartFile("video",
                "video.mp4",
                "video/mp4",
                Files.readAllBytes(realVideo.getFile().toPath()));

        String videoPostContent = objectMapper.writeValueAsString(new VideoPostSaveDto("test title", "test description", "test.test1@test.test",
                1L,"idol","boy",1L));

        MockMultipartFile json = new MockMultipartFile("post","video_post","application/json",videoPostContent.getBytes(StandardCharsets.UTF_8));

        MvcResult result = this.mockMvc.perform(multipart("/api/v1/file")
                .file(video)
                .file(json)
                .header("X-AUTH-TOKEN", tokenProvider.createToken("dancer_test1@test.test",Role.DANCER.getKey()))
                .contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String output = result.getResponse().getContentAsString();
        System.out.println("=====resonse content======");
        System.out.println(output);

        /* Get Video Post Test */
        MvcResult vpResult = this.mockMvc.perform(get("/api/v1/post/"+output)
                .header("X-AUTH-TOKEN", tokenProvider.createToken("dancer_test1@test.test",Role.DANCER.getKey())))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("vpResult = " + vpResult.getResponse().getContentAsString());

    }


    @Test
    @DisplayName("포스트를 게시하면 포스트의 내용과 동영상, 썸네일을 확인할 수 있다.")
    public void getPostTest() {

    }

    @Test
    @DisplayName("스트리밍 여부 확인")
    public void doStramingTest() {

    }

    @Test
    @DisplayName("s3 유틸 여부 변경")
    public void doUtilCheck() {

    }

}