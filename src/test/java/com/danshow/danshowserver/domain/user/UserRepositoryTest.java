package com.danshow.danshowserver.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DancerRepository dancerRepository;

    @Test
    @DisplayName("멤버로 넣었을 때 유저 레포로 검색되는지 확인")
    public void memberRepositoryReturnMember() {

        Member member = Member.builder()
                .email("testmember@test.com")
                .nickname("testnick")
                .membership(true)
                .name("junho")
                .build();

        memberRepository.save(member);

        User findUser = userRepository.findByEmail("testmember@test.com");

        assertThat(findUser.getEmail()).isEqualTo(member.getEmail());
        assertThat(findUser.getNickname()).isEqualTo(member.getNickname());
        assertThat(findUser).isEqualTo(member);

        System.out.println("::::::: '==' 비교 :::::::::");
        System.out.println(member==findUser);
    }

    @Test
    @DisplayName("instanceof 를 통한 유저 클래스의 멤버/댄서 변")
    public void getDtype() {

        Member member = Member.builder()
                .email("testmember@test.com")
                .nickname("testnick")
                .membership(true)
                .name("junho")
                .build();

        memberRepository.save(member);

        User findUser = userRepository.findByEmail("testmember@test.com");

        assertThat(findUser instanceof Member).isTrue();
        assertThat(findUser instanceof Dancer).isFalse();

    }

}