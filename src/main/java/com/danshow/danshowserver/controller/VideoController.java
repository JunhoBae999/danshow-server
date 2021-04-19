package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.service.video_service.VideoServiceInterface;
import com.danshow.danshowserver.web.dto.VideoPostResponseDto;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoServiceInterface videoService;

    @PostMapping("/api/v1/file")
    public ResponseEntity<String> fileUpload(@RequestPart("video")  MultipartFile video,
                                     @RequestPart("post")VideoPostSaveDto videoPostSaveDto,
                                     @RequestParam("userID") String userId, @RequestPart(value = "thumbnail",required = false) MultipartFile image)  {
        try {
            videoService.save(video,videoPostSaveDto,userId,image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<VideoPostResponseDto> getPost(@PathVariable Long id) {
        VideoPost vp = videoService.getVideoPost(id);
        VideoPostResponseDto videoPostResponseDto = VideoPostResponseDto.createVideoPostResponseDto(vp);
        return new ResponseEntity<>(videoPostResponseDto, HttpStatus.OK);
    }


    @GetMapping("/api/v1/videos/{name}")
    public ResponseEntity<ResourceRegion> getVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) throws IOException {
        return null;
    }




}
