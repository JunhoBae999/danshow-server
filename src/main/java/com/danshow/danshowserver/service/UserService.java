package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService{
    private final UserRepository userRepository;

    public Long save(UserDto.save saveDto) {
        return userRepository.save(saveDto.toEntity()).getId();
    }
}
