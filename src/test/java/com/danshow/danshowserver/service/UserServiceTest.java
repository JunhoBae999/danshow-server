package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.web.dto.MemberSaveRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void saveTest() {
        String email = "abc@a.com";
        MemberSaveRequestDto requestDto = new MemberSaveRequestDto("abc@a.com","1234","nick","name",
                Boolean.FALSE, "description","picture");

        userService.save(requestDto);

        Member member = memberRepository.findAll().get(0);
        assertThat(member.getEmail()).isEqualTo(email);
    }
}