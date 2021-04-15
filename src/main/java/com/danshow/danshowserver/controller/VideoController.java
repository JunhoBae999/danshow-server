package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.video.Video;
import com.danshow.danshowserver.service.VideoService;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/api/v1/file")
    public ResponseEntity fileUpload(@RequestPart("video")  MultipartFile video,
                                     @RequestPart("post")VideoPostSaveDto videoPostSaveDto,
                                     @RequestParam("userID") String userId)  {

        //TODO: 유저 검색 방식 확정 필요
        try {
            videoService.save(video,videoPostSaveDto,userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
