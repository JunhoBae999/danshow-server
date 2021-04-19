package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

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

    @BeforeEach
    public void setUp() {

        Member member = Member.builder()
                .membership(true)
                .email("test.test@test.test")
                .nickname("tester")
                .name("testjunho")
                .build();

        memberRepository.save(member);

        Dancer dancer = Dancer.builder()
                .dancer_description("test dancer desc")
                .email("dancer_test@test.test")
                .nickname("testdencer")
                .name("testerdancer")
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


        String videoPostContent = objectMapper.writeValueAsString(new VideoPostSaveDto("test title", "test description", "test.test@test.test",
                1L,"idol","boy",1L,"lecture"));

        MockMultipartFile json = new MockMultipartFile("post","video_post","application/json",videoPostContent.getBytes(StandardCharsets.UTF_8));

        MvcResult result = this.mockMvc.perform(multipart("/api/v1/file")
                .file(video)
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



}