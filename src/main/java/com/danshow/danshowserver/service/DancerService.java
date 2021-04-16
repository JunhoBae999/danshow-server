package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.DancerUpdateRequestDto;
import com.danshow.danshowserver.web.dto.MemberUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class DancerService {
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final DancerRepository dancerRepository;

    @Transactional
    public Long update(DancerUpdateRequestDto requestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        Dancer dancer;
        if(sessionUser == null) //only for testing
            dancer = dancerRepository.findByEmail(requestDto.getEmail());
        else
            dancer = dancerRepository.findByEmail(sessionUser.getEmail());
        dancer = requestDto.toEntity(dancer); //update
        return dancer.getId();
    }
}
