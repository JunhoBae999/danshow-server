package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.user.MemberSaveRequestDto;
import com.danshow.danshowserver.web.dto.user.MemberUpdateRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DancerRepository dancerRepository;

    @Before
    public void cleanUp() {
        userRepository.deleteAll();
        MemberSaveRequestDto requestDto = new MemberSaveRequestDto("abc@a.com","1234",
                "nick","name",
                Boolean.FALSE, "description","picture");
        memberService.save(requestDto);
    }

    @Test
    public void saveTest() {
        //given
        String email = "abc@a.com";

        //when
        Member member = memberRepository.findAll().get(0);

        //then
        assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    public void memberUpdateTest() {
        //given
        MemberUpdateRequestDto memberUpdateRequestDto = new MemberUpdateRequestDto("abc@a.com",
                "new_nickname",true,null);
        memberService.update(memberUpdateRequestDto);

        //when
        Member member = memberRepository.findByEmail("abc@a.com");

        //then
        assertThat(member.getNickname()).isEqualTo("new_nickname");
        assertThat(member.getMembership()).isTrue();
        assertThat(member.getProfile_description()).isEqualTo("description");
    }

    @Test
    @DisplayName("멤버에서 댄서로")
    public void memberToDancer() {
        //given
        memberService.toDancer("abc@a.com");

        //when
        Dancer dancer = dancerRepository.findByEmail("abc@a.com");

        //then
        assertThat(dancer.getName()).isEqualTo("name");
        assertThat(memberRepository.findByEmail("abc@a.com")).isNull();
    }
}