package com.danshow.danshowserver.web.dto.crew;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrewSaveRequestDto {
    private String title;
    private String description;

    @Builder
    public CrewSaveRequestDto(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
