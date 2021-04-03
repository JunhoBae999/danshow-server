package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.web.dto.MemberSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService{

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(MemberSaveRequestDto requestDto) {

        return memberRepository.save(requestDto.toEntity()).getId();

    }
}
