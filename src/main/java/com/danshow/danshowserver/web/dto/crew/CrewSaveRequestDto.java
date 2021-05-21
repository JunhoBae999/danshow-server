package com.danshow.danshowserver.web.dto.crew;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrewSaveRequestDto {
    private String title;
    private String description;
    private String image_url;

    @Builder
    public CrewSaveRequestDto(String title, String description, String image_url) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
    }

}
