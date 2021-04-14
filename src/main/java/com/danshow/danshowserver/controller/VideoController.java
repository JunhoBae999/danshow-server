package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.video.Video;
import com.danshow.danshowserver.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/api/v1/file")
    public String fileUpload(@RequestParam MultipartFile video) {

        //videoService.save(video);
        return null;
    }




}
