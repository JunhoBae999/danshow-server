package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.user.MemberResponseDto;
import com.danshow.danshowserver.web.dto.user.MemberSaveRequestDto;
import com.danshow.danshowserver.web.dto.user.MemberUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import java.util.Optional;

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

    @Transactional
    public MemberResponseDto findById(Long id) {
        User user = (User) userRepository.findById(id).orElse(null);
        if(user == null) {
            return MemberResponseDto.builder().build();
        } else if(user instanceof Member) {
            Member member = (Member) memberRepository.findById(id).orElse(null);
            return MemberResponseDto.builder()
                    .email(member.getEmail())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .membership(member.getMembership())
                    .profile_description(member.getProfile_description())
                    .profile_picture(member.getProfile_picture())
                    .build();
        } else {
            Dancer dancer = (Dancer) dancerRepository.findById(id).orElse(null);
            return MemberResponseDto.builder()
                    .email(dancer.getEmail())
                    .name(dancer.getName())
                    .nickname(dancer.getNickname())
                    .profile_description(dancer.getDancer_description())
                    .profile_picture(dancer.getDancer_picture())
                    .build();
        }

    }

}
