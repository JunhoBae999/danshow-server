package com.danshow.danshowserver.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Thumbnail {
    private String image_url;
    private String title;
    private String thumbnailText;

    @Builder
    public Thumbnail(String image_url, String title, String thumbnailText) {
        this.image_url =image_url;
        this.title = title;
        this.thumbnailText = thumbnailText.substring(0,100); // 앞의 100글자만 저장
    }
}
