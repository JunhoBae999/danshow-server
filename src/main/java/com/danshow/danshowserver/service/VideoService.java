package com.danshow.danshowserver.service;

import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.DancerRepository;
import com.danshow.danshowserver.domain.video.Video;
import com.danshow.danshowserver.domain.video.VideoPost;
import com.danshow.danshowserver.domain.video.VideoPostRepository;
import com.danshow.danshowserver.domain.video.VideoRepository;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    private final VideoPostRepository videoPostRepository;

    private final DancerRepository dancerRepository;

    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception {

        Dancer dancer = dancerRepository.findByEmail(userId);
        Video uploadedVideo = uploadVideo(video);
        VideoPost videoPost = VideoPost.createVideoPost(videoPostSaveDto, dancer, uploadedVideo);

    }

    public Video uploadVideo(MultipartFile video) throws Exception {

            String originalFilename = video.getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            String filename = uuid.toString()+"-"+originalFilename;

            //s3 처리로직
            //String savePath = s3Util.upload(video,filename,"video")

            //로컬 처리로직 : 실행 위치의 files 폴더에 저
            String savePath = System.getProperty("user.dir") + "\\files";

            if(!new File(savePath).exists()) {
                try{
                    new File(savePath).mkdir();
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }

            String filePath = savePath + "\\" +filename;

            video.transferTo(new File(filePath));

            Video savedVideo = Video.builder()
                    .filename(filename)
                    .filePath(filePath)
                    .originalFileName(originalFilename)
                    .build();

            return savedVideo;

    }




}
