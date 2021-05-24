package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
서버 배포용 service
 */

@Profile("release")
@RequiredArgsConstructor
@Service
public class VideoServiceRelease implements VideoServiceInterface{

    private final UserRepository userRepository;
    private final VideoPostRepository videoPostRepository;
    private final S3Uploader s3Uploader;
    private final VideoFileUtils videoFileUtils;

    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception {

        User dancer = userRepository.findByEmail(userId);
        AttachFile uploadedVideo = uploadFile(video,"video");
        AttachFile uploadImage = uploadThumbnail(video);
        VideoPost videoPost = VideoPost.createVideoPost(videoPostSaveDto, dancer, uploadedVideo,  uploadImage);
        videoPostRepository.save(videoPost);
    }

    @Override
    public AttachFile uploadFile(MultipartFile video,String saveFolder) throws IOException {
        String originalFilename = video.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString()+"-"+originalFilename;

        String filePath = s3Uploader.upload(video, saveFolder); //s3업로드 후 주소 받아옴

        AttachFile savedVideo = AttachFile.builder()
                .filename(filename) //TODO 현재 uuid 적용 하지 않은채 업로드하고, filePath에 파일명까지 다 저장되기 때문에 현재 의미가 없음
                .filePath(filePath)
                .originalFileName(originalFilename)
                .build();

        return savedVideo;
    }

    public AttachFile uploadThumbnail(MultipartFile video) throws IOException {
        String thumbnailPath = videoFileUtils.extractThumbnail(video);
        String originalFileName = video.getOriginalFilename();
        String fileName = thumbnailPath
                .substring(thumbnailPath
                        .indexOf(originalFileName.substring(0,originalFileName.indexOf("."))+"/"));
        String filePath = s3Uploader.upload(thumbnailPath,fileName,"image");

        AttachFile savedImage = AttachFile.builder()
                .filename(fileName) //TODO uuid 적용안됨
                .filePath(filePath)
                .originalFileName(fileName)
                .build();

        File deleteFile = new File(thumbnailPath);
        if(deleteFile.exists()) {
            deleteFile.delete();
        }
        return savedImage;
    }
    @Override
    public AttachFile getVideo(Long id) {
        return null;
    }

    @Override
    public VideoPost getVideoPost(Long id) {
        return null;
    }

    @Override
    public ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) {
        return null;
    }
}
