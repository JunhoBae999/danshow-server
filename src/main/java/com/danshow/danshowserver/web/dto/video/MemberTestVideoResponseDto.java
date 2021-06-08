package com.danshow.danshowserver.web.dto.video;

import com.danshow.danshowserver.domain.video.post.MemberTestVideoPost;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberTestVideoResponseDto {

    private String title;
    private String filePath;
    private Long score;

}
