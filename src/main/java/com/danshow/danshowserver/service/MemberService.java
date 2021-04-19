package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.MemberSaveRequestDto;
import com.danshow.danshowserver.web.dto.MemberUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final DancerRepository dancerRepository;

    @Transactional
    public Long save(MemberSaveRequestDto requestDto) {
        return memberRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(MemberUpdateRequestDto requestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        Member member;
        if(sessionUser == null) //only for testing
            member = memberRepository.findByEmail(requestDto.getEmail());
        else
            member = memberRepository.findByEmail(sessionUser.getEmail());
        member = requestDto.toEntity(member); //update
        return member.getId();
    }

    @Transactional
    public Long toDancer(String email) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        Member member;
        try {
            if(sessionUser == null) //only for testing
                member = memberRepository.findByEmail(email);
            else
                member = memberRepository.findByEmail(sessionUser.getEmail());
            Dancer dancer = Dancer.builder()
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .name(member.getName())
                    .dancer_description(member.getProfile_description())
                    .dancer_picture(member.getProfile_picture())
                    .build();
            dancerRepository.save(dancer);
            memberRepository.delete(member);
            return dancer.getId();
        } catch (Exception e){
            return -1L;
        }

    }

}
