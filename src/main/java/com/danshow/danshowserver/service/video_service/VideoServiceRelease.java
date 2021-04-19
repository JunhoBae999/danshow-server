package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
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

    @Override
    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId, MultipartFile image) throws Exception {


    }

    @Override
    public AttachFile uploadFile(MultipartFile video) throws IOException {
        String originalFilename = video.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString()+"-"+originalFilename;

        //s3 처리로직
        //String savePath = s3Util.upload(video,filename,"video")
        //로컬 처리로직 : 실행 위치의 files 폴더에 저

        String savePath = System.getProperty("user.dir") + "/files";

        if(!new File(savePath).exists()) {
            try{
                new File(savePath).mkdir();
            }catch (Exception e) {
                e.getStackTrace();
            }
        }

        String filePath = savePath + "/" +filename;

        video.transferTo(new File(filePath));

        AttachFile savedVideo = AttachFile.builder()
                .filename(filename)
                .filePath(filePath)
                .originalFileName(originalFilename)
                .build();

        return savedVideo;
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
