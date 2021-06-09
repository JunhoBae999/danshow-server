package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.service.video_service.AnalyzeService;
import com.danshow.danshowserver.service.video_service.VideoServiceInterface;
import com.danshow.danshowserver.web.dto.VideoPostResponseDto;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.danshow.danshowserver.web.dto.video.MemberTestVideoResponseDto;
import com.danshow.danshowserver.web.dto.video.VideoMainResponseDto;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.List;

@Api(tags = {"2.Video"})
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoServiceInterface videoService;
    private final TokenProvider tokenProvider;
    private final AnalyzeService analyzeService;
    private final S3Uploader s3Uploader;

    /**
     * 댄서가 비디오를 업로드 하는 경우입니다. 업로드 할 시 그대로 저장되며, 음원 파일이 추출됩니다.
     * @param video
     * @param videoPostSaveDto
     * @param Jwt
     * @return
     */
    @ApiOperation(value = "Upload Video Post",notes = "Request with Video file, Video post request object, User, Thumbnail image to create post")
    @PostMapping("/api/v1/file")
    public ResponseEntity<String> fileUpload(@ApiParam(value = "비디오 파일",required = true) @RequestPart("video")  MultipartFile video,
                                     @ApiParam(value = "비디오 포스트 요청 json",required = true) @RequestPart("post") VideoPostSaveDto videoPostSaveDto,
                                     @ApiParam(value = "JWT토큰", required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt)  {

        String email = tokenProvider.getUserPk(Jwt);
        try {
            Long videoPostId = videoService.save(video,videoPostSaveDto,email);
            return new ResponseEntity<>(videoPostId.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("fail", HttpStatus.OK);
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

    @ApiOperation(value = "메인화면 비디오 썸네일", notes = "메인화면의 비디오 썸네일을 제공합니다.")
    @GetMapping("/api/v1/videos/main")
    public ResponseEntity<VideoMainResponseDto> videoMain() {
        VideoMainResponseDto responseDto = videoService.mainPage();
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }


    @ApiOperation(value = "유저 테스트 비디오 선택", notes = "유저가 테스트할 비디오를 선택하는 경우 음원파일을 제공합니다.")
    @GetMapping("/api/v1/videos/{id}/music")
    public ResponseEntity<String> getSelectedVideoMp4(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id) {
        return new ResponseEntity<>(videoService.getMusicUrl(id),HttpStatus.OK);
    }

    @ApiOperation(value = "유저 테스트 비디오 업로드",notes = "유저가 음원과 함께 녹화한 비디오를 업로드합니다.")
    @PostMapping("/api/v1/member-test/{id}")
    public ResponseEntity<Void> uploadUserTestVideo(@ApiParam(value = "영상 식별자",required = true) @PathVariable Long id,
                                                          @ApiParam(value = "유저 테스트 비디오") @RequestPart MultipartFile userTestVideo,
                                                          @ApiParam(value = "JWT토큰", required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) throws IOException {

        analyzeService.getAnalyzedVideo(userTestVideo,id,Jwt);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "테스트용 미러링 엔드포인트", notes = "요청받은 데이터를 그대로 돌려줍니다.")
    @PostMapping("/mirror")
    public byte[] getMirror(@RequestBody byte[] videos) throws InterruptedException {
        Thread.sleep(30000);
        return videos;
    }

    @ApiOperation(value = "멤버 테스트 비디오 목록", notes = "유저의 테스트 비디오 목록을 넘겨줍니다.")
    @GetMapping("api/v1/videos/test")
    public ResponseEntity<List<MemberTestVideoResponseDto>> getMemberTestVideoList(
            @ApiParam(value = "JWT토큰", required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {

        String email = tokenProvider.getUserPk(Jwt);
        return new ResponseEntity<>(videoService.getMemberTestVideoList(email),HttpStatus.OK);

    }
}
