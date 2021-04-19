package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.video.AttachFile;
import lombok.Data;

@Data
public class VideoResponseDto {
    private Long videoId;
    private String filename;
    private String filePath;
    private Long length;

    public VideoResponseDto(AttachFile video) {
        videoId = video.getId();
        filePath = video.getFilePath();
        filename = video.getFilename();
    }
}

