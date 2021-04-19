package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.video.AttachFile;
import lombok.Data;


@Data
public class ImageResponseDto {
    private Long imageId;
    private String filename;
    private String filePath;

    public ImageResponseDto(AttachFile image) {
        imageId = image.getId();
        filePath = image.getFilePath();
        filename = image.getFilename();
    }
}
