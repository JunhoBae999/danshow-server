package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.crew.CrewRepository;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.web.dto.crew.CrewResponseDto;
import com.danshow.danshowserver.web.dto.crew.CrewSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class CrewService {

    private final UserRepository userRepository;
    private final DancerRepository dancerRepository;
    private final HttpSession httpSession;
    private final CrewRepository crewRepository;

    @Transactional
    public void save(CrewSaveRequestDto crewSaveRequestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        crewRepository.save(Crew.builder()
                .description(crewSaveRequestDto.getDescription())
                .crew_profile_image(crewSaveRequestDto.getImage_url())
                .dancer(dancerRepository.findByEmail(sessionUser.getEmail())).build());
    }

    @Transactional
    public void update(CrewSaveRequestDto crewSaveRequestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        Crew crew = crewRepository.findByDancer(dancerRepository.findByEmail(sessionUser.getEmail()));

        crew.setCrew_profile_image(crewSaveRequestDto.getImage_url());
        crew.setDescription(crewSaveRequestDto.getDescription());
    }

    @Transactional
    public CrewResponseDto findById(Long id) {
        Crew crew = crewRepository.findById(id).orElse(null);
        return CrewResponseDto.builder()
                .crew_profile_image(crew.getCrew_profile_image())
                .description(crew.getDescription())
                .build();
    }
}
