package com.danshow.danshowserver.web.controller;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MembershipTestController {

    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final DancerRepository dancerRepository;

    @GetMapping("/need-membership")
    public String checkMembership() {
        /*멤버십 있는 멤버이거나, 댄서인지 체크 후 데이터 넘기기*/
        /*로그인이 아에 안된 사람이면 스프링 시큐리티에서 접근을 막음.*/
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member = memberRepository.findByEmail(user.getEmail());
        if(member==null || member.getMembership()) { //멤버가 아님(댄서). 혹은 멤버십 있는 멤버.
            return "Right data";
        }
        //멤버십 없는 멤버. 클라이언트 쪽에서 멤버십 가입하도록 유도.
        return "You don't have access.";
    }

    @GetMapping("/request-membership")
    public String addMembership() {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member = memberRepository.findByEmail(user.getEmail());
        if(member!=null) {
            member.setMembership(true);
            memberRepository.save(member);
            return "ok";
        }
        return "false";
    }

    @GetMapping("/check-my-profile")
    public String checkUserProfile() {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member = memberRepository.findByEmail(user.getEmail());
        if(member==null) {
            return user.getEmail()+" dancer";
        }
        return user.getEmail()+" member "+member.getMembership();
    }

    @GetMapping("/upgrade-to-dancer")
    public String upgradeToDancer() {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member = memberRepository.findByEmail(user.getEmail());
        if(member!=null) {
            dancerRepository.save(Dancer.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .name(member.getName())
            .dancer_description(member.getProfile_description())
            .dancer_picture(member.getProfile_picture())
            .build());
            memberRepository.delete(member);
            return "The role has been successfully converted";
        }
        return "You're already dancer";

    }
}
