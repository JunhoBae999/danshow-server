package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.DancerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class VideoRepositoryTest {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    DancerRepository dancerRepository;

    private static Dancer dancer;

    @BeforeAll
    public void setUp() {
        dancer = Dancer.builder()
                .nickname("testA")
                .email("test@test.com")
                .dancer_description("test dancer description")
                .name("junho")
                .build();

        dancerRepository.save(dancer);
    }



}