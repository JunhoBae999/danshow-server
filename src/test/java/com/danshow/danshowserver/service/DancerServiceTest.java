package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.Role;
import com.danshow.danshowserver.service.user_service.DancerService;
import com.danshow.danshowserver.web.dto.user.DancerUpdateRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DancerServiceTest {
    @Autowired
    DancerService dancerService;
    @Autowired
    DancerRepository dancerRepository;

    @Before
    public void cleanUp() {
        dancerRepository.deleteAll();
        dancerRepository.save(Dancer.builder()
                .email("abc@a.com")
                .nickname("nickname")
                .name("name")
                .role(Role.DANCER)
                .dancer_description("No description")
                .dancer_picture("picture url")
                .build());
    }

    @Test
    public void dancerUpdate() {
        //given
        dancerService.update(DancerUpdateRequestDto.builder()
                .dancer_description("New description").build(),"abc@a.com");
        //when
        Dancer dancer = dancerRepository.findByEmail("abc@a.com");

        //then
        assertThat(dancer.getDancer_description()).isEqualTo("New description");

    }

}