package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.danshow.danshowserver.web.dto.video.VideoMainResponseDto;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface VideoServiceInterface {

    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception;

    public VideoMainResponseDto mainPage();

    public AttachFile uploadFile(MultipartFile video, String customFileName, String savePath) throws IOException;

    public AttachFile getVideo(Long id);

    public VideoPost getVideoPost(Long id);

    ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException;

    public File uploadMemberTestVideo(MultipartFile memberVideo, Long id) throws IOException;
}
