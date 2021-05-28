package com.danshow.danshowserver.web.dto.video;

import com.danshow.danshowserver.web.dto.Thumbnail;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoMainResponseDto {

    List<Thumbnail> videoThumbnailList;
}
