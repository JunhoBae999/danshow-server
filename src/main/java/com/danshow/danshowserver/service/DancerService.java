package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.user.DancerUpdateRequestDto;
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
    public Long update(DancerUpdateRequestDto requestDto, String email) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        Dancer dancer = dancerRepository.findByEmail(email);
        dancer = requestDto.toEntity(dancer); //update
        return dancer.getId();
    }
}
