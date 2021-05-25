package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.domain.video.FileRepository;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class VideoServiceImplLocal implements VideoServiceInterface {

    private final FileRepository fileRepository;

    private final VideoPostRepository videoPostRepository;

    private final DancerRepository dancerRepository;

    private final UserRepository userRepository;

    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception {

        User dancer = userRepository.findByEmail(userId);
        String savePath = System.getProperty("user.dir") + "/files";

        AttachFile uploadedVideo = uploadFile(video,"",savePath);
        AttachFile uploadImage = uploadFile(video,"",savePath); //TODO 로컬 구현은 우선순위가 아닌거같아 일단 이렇게 남깁니다
        VideoPost videoPost = VideoPost.createVideoPost(videoPostSaveDto, dancer, uploadedVideo,  uploadImage);
        videoPostRepository.save(videoPost);
    }

    public AttachFile uploadFile(MultipartFile video, String customFileName, String savePath) throws IOException {

        String originalFilename = video.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString()+"-"+originalFilename;

        String filePath = getFilePath(filename, savePath);
        AttachFile savedFile = getAttachFile(video, originalFilename, filename, filePath);

        return savedFile;
    }

    private String getFilePath(String filename, String savePath) {
        if(!new File(savePath).exists()) {
            try{
                new File(savePath).mkdir();
            }catch (Exception e) {
                e.getStackTrace();
            }
        }
        String filePath = savePath + "/" + filename;
        return filePath;
    }

    private AttachFile getAttachFile(MultipartFile video, String originalFilename, String filename, String filePath) throws IOException {
        video.transferTo(new File(filePath));

        AttachFile savedFile = AttachFile.builder()
                    .filename(filename)
                    .filePath(filePath)
                    .originalFileName(originalFilename)
                    .build();

        System.out.println("here!");
        System.out.println(originalFilename);
        return savedFile;
    }


    public AttachFile getVideo(Long id) {
        //TODO : 에러 익셉션 처리 필
        return (AttachFile) fileRepository.findById(id).orElse(null);
    }

    public VideoPost getVideoPost(Long id) {
        //TODO : 에러 익셉션 처리 필요
        return (VideoPost) videoPostRepository.findById(id).orElse(null);
    }

    @Override
    public ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException{

        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();

        HttpRange httpRange = headers.getRange().stream().findFirst().get();
        if(httpRange != null) {
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long ragneLength = Long.min(chunkSize,end-start+1);
            return new ResourceRegion(video,start,ragneLength);
        }else {
            long rangeLength = Long.min(chunkSize,contentLength);
            return new ResourceRegion(video,0,rangeLength);
        }
    }




}
