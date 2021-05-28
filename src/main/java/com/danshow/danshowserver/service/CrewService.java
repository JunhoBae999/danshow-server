package com.danshow.danshowserver.service;

import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.crew.CrewRepository;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.web.dto.Thumbnail;
import com.danshow.danshowserver.web.dto.crew.CrewResponseDto;
import com.danshow.danshowserver.web.dto.crew.CrewSaveRequestDto;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CrewService {

    private final UserRepository userRepository;
    private final DancerRepository dancerRepository;
    private final CrewRepository crewRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void save(MultipartFile image, CrewSaveRequestDto crewSaveRequestDto, String email) throws IOException {

        String image_url = s3Uploader.upload(image,"image");
        crewRepository.save(Crew.builder()
                .description(crewSaveRequestDto.getDescription())
                .crew_profile_image(image_url)
                .dancer(dancerRepository.findByEmail(email)).build());
    }

    @Transactional
    public void update(MultipartFile image, CrewSaveRequestDto crewSaveRequestDto, String email) throws IOException {

        String image_url = s3Uploader.upload(image,"image");
        Crew crew = crewRepository.findByDancer(dancerRepository.findByEmail(email));
        crew.setCrew_profile_image(image_url);
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

    @Transactional
    public List<Thumbnail> crewMainList() {
        List<Thumbnail> crewThumbnailList = new ArrayList<>();
        List<Crew> all = crewRepository.findAll();
        for (int i = 0; i < all.size() && i < 6; i++) {
            // 최대 6개까지만 리스트에 담기
            crewThumbnailList.add(makeThumbnail(all.get(i)));
        }
        return crewThumbnailList;
    }

    public Thumbnail makeThumbnail(Crew crew) {
        return Thumbnail.builder()
                .title(crew.getTitle())
                .image_url(crew.getCrew_profile_image())
                .thumbnailText(crew.getDescription())
                .build();
    }
}
