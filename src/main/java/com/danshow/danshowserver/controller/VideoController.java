package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.service.video_service.VideoServiceInterface;
import com.danshow.danshowserver.web.dto.VideoPostResponseDto;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.MalformedURLException;

@Api(tags = {"2.Video"})
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoServiceInterface videoService;

    @ApiOperation(value = "비디오 포스트 업로드",notes = "비디오, 비디오 포스트 요청 객체, 유저, 썸네일 이미지를 받아 업로드합니다.")
    @PostMapping("/api/v1/file")
    public ResponseEntity<String> fileUpload(@ApiParam(value = "비디오 파일",required = true) @RequestPart("video")  MultipartFile video,
                                     @ApiParam(value = "비디오 포스트 요청 json",required = true) @RequestPart("post")VideoPostSaveDto videoPostSaveDto,
                                     @ApiParam(value = "유저", required = true) @RequestParam("userID") String userId, @RequestPart(value = "thumbnail",required = false) MultipartFile image)  {
        try {
            videoService.save(video,videoPostSaveDto,userId,image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "비디오 포스트 조회", notes = "비디오 포스트를 조회합니다.")
    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<VideoPostResponseDto> getPost(@ApiParam(value = "포스트 식별")@PathVariable Long id) {
        VideoPost vp = videoService.getVideoPost(id);
        VideoPostResponseDto videoPostResponseDto = VideoPostResponseDto.createVideoPostResponseDto(vp);
        return new ResponseEntity<>(videoPostResponseDto, HttpStatus.OK);
    }


    @ApiOperation(value = "비디오 스트리밍", notes = "용량이 큰 비디오를 스트리밍 합니다. bytes 나누어 전송합니다.")
    @GetMapping("/api/v1/videos/{id}")
    public ResponseEntity<ResourceRegion> getVideo(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id, @RequestHeader HttpHeaders headers) throws IOException {

        AttachFile video = videoService.getVideo(id);

        UrlResource responseVideo = new UrlResource(video.getFilePath());

        ResourceRegion region = videoService.resourceRegion(responseVideo,headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(responseVideo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @ApiOperation(value = "비디오 전체 스트리밍" , notes = "용량이 작은 비디오를 한번에 응답합니다.")
    @GetMapping("/api/v1/videos/{id}/full")
    public ResponseEntity<UrlResource> getFullVideo(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id) throws MalformedURLException {
        AttachFile video = videoService.getVideo(id);

        UrlResource resVideo = new UrlResource(video.getFilePath());
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resVideo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resVideo);
    }


}
