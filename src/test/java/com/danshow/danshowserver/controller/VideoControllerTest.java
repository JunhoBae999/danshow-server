package com.danshow.danshowserver.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void videoUploadTest() throws Exception {
        MockMultipartFile video = new MockMultipartFile("file",
                "video.mp4",
                "video/mp4",
                "video test".getBytes(StandardCharsets.UTF_8));

        this.mockMvc.perform(multipart("/file").file(video))
                .andDo(print())
                .andExpect(status().isOk());
    }



}