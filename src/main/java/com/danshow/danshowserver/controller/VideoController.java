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

    @ApiOperation(value = "Upload Video Post",notes = "Request with Video file, Video post request object, User, Thumbnail image to create post")
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

    @ApiOperation(value = "Get Video Post", notes = "Get Video Post By Id")
    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<VideoPostResponseDto> getPost(@ApiParam(value = "포스트 식별")@PathVariable Long id) {
        VideoPost vp = videoService.getVideoPost(id);
        VideoPostResponseDto videoPostResponseDto = VideoPostResponseDto.createVideoPostResponseDto(vp);
        return new ResponseEntity<>(videoPostResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Video Post", notes = "delete video post only if user")
    @PostMapping("/api/v1/post/{id}/delete")
    public ResponseEntity deletePost(@ApiParam(value = "post id") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    @ApiOperation(value = "Video Streaming", notes = "Stream the Video with Big size as bytes array.")
    @GetMapping("/api/v1/videos/{id}")
    public ResponseEntity<ResourceRegion> getVideo(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id, @RequestHeader HttpHeaders headers) throws IOException {

        AttachFile video = videoService.getVideo(id);

        UrlResource responseVideo = new UrlResource(video.getFilePath());

        ResourceRegion region = videoService.resourceRegion(responseVideo,headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(responseVideo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @ApiOperation(value = "Video Straming Totally" , notes = "Stream the Video with small size as array")
    @GetMapping("/api/v1/videos/{id}/full")
    public ResponseEntity<UrlResource> getFullVideo(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id) throws MalformedURLException {
        AttachFile video = videoService.getVideo(id);

        UrlResource resVideo = new UrlResource(video.getFilePath());
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resVideo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resVideo);
    }


}
