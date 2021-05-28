package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.web.dto.Thumbnail;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.danshow.danshowserver.web.dto.video.VideoMainResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
서버 배포용 service
 */

@RequiredArgsConstructor
@Service
public class VideoServiceRelease implements VideoServiceInterface{

    private final UserRepository userRepository;
    private final VideoPostRepository videoPostRepository;
    private final S3Uploader s3Uploader;
    private final VideoFileUtils videoFileUtils;

    //단순 영상 저장용. 예를들어 분석이 필요없는 단순 강의영상.
    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception {

        User dancer = userRepository.findByEmail(userId);

        //uuid 붙인 파일명 생성
        UUID uuid = UUID.randomUUID();
        String uuidFileName = uuid+"-"+video.getOriginalFilename();

        AttachFile uploadedVideo = uploadFile(video,uuidFileName,"video");
        AttachFile uploadImage = uploadThumbnail(video, uuidFileName);
        VideoPost videoPost = VideoPost.createVideoPost(videoPostSaveDto, dancer, uploadedVideo,  uploadImage);
        videoPostRepository.save(videoPost);
    }


    public AttachFile uploadFile(MultipartFile video,String customFileName, String saveFolder) throws IOException {
        String originalFilename = video.getOriginalFilename();

        String filePath = s3Uploader.upload(video,customFileName, saveFolder); //s3업로드 후 주소 받아옴

        AttachFile savedVideo = AttachFile.builder()
                .filename(customFileName)
                .filePath(filePath)
                .originalFileName(originalFilename)
                .build();
        return savedVideo;
    }

    //비디오를 분할해서 S3 업로드 후 주소 return
    public List<String> splitUpload(MultipartFile video, Integer chunks) throws Exception {
        
        //임시폴더에 전체 영상 저장
        File fileJoinPath = new File(System.getProperty("user.dir") + "/tmp");
        if (!fileJoinPath.exists()) {
            fileJoinPath.mkdirs();
        }
        String temporalFilePath = System.getProperty("user.dir") + "/tmp/"+video.getOriginalFilename();
        video.transferTo(new File(temporalFilePath));
        
        //전체 영상을 임시폴더에 분할 저장
        List<String> splitFiles = videoFileUtils.splitFile(temporalFilePath,video.getOriginalFilename(),
                fileJoinPath.toString(),chunks);
        
        //분할영상들을 S3에 업로드
        List<String> s3FilePathList = new ArrayList<String>();
        for(String filePath : splitFiles) {
            s3FilePathList.add(
                    s3Uploader.upload(filePath, filePath.substring(filePath.lastIndexOf("/") + 1), "splitVideo"));
        }
        //임시파일들 전부 제거
        videoFileUtils.deleteFile(temporalFilePath);
        for(String filePath : splitFiles) {
            videoFileUtils.deleteFile(filePath);
        }
        return s3FilePathList;
    }

    public AttachFile uploadThumbnail(MultipartFile video, String customFileName) throws IOException {
        String thumbnailPath = videoFileUtils.extractThumbnail(video,customFileName);
        String fileName = thumbnailPath
                .substring(thumbnailPath
                        .lastIndexOf("/"),-1);
        
        //S3업로드 된 주소
        String filePath = s3Uploader.upload(thumbnailPath,fileName,"image");

        AttachFile savedImage = AttachFile.builder()
                .filename(fileName)
                .filePath(filePath)
                .originalFileName(video.getOriginalFilename())
                .build();

        //썸네일 업로드 후 임시파일 삭제
        File deleteFile = new File(thumbnailPath);

        if(deleteFile.exists()) {
            deleteFile.delete();
        }

        return savedImage;
    }

    public VideoMainResponseDto mainPage() {
        List<Thumbnail> thumbnailList = new ArrayList<>();
        List<VideoPost> all = videoPostRepository.findAll();
        for (int i = 0; i < all.size() && i < 6; i++) {
            // 최대 6개까지만 리스트에 담기
            thumbnailList.add(makeThumbnail(all.get(i)));
        }
        return VideoMainResponseDto.builder()
                .videoThumbnailList(thumbnailList)
                .build();
    }

    public Thumbnail makeThumbnail(VideoPost videoPost) {
        return Thumbnail.builder()
                .title(videoPost.getTitle())
                .image_url(videoPost.getImage().getFilePath())
                .thumbnailText(videoPost.getDescription())
                .build();
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
