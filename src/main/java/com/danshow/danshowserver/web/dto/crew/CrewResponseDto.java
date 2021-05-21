package com.danshow.danshowserver.web.dto.crew;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class CrewResponseDto {
    //TODO 나중에 dancer랑 member 정보 넘기게 바꿔야 할듯
    private String title;
    private String description;
    private String crew_profile_image;

}
