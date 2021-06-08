package com.danshow.danshowserver.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Thumbnail {
    private Long id;
    private String image_url;
    private String title;
    private String thumbnailText;

    @Builder
    public Thumbnail(Long videoPostId, String image_url, String title, String thumbnailText) {
        this.id = videoPostId;
        this.image_url =image_url;
        this.title = title;
        this.thumbnailText = thumbnailText;
    }
}
