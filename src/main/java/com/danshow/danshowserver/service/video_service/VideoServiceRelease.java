package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.aspect.TimeCheck;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.FileRepository;
import com.danshow.danshowserver.domain.video.post.MemberTestVideoPost;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.domain.video.repository.VideoPostRepository;
import com.danshow.danshowserver.web.dto.Thumbnail;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import com.danshow.danshowserver.web.dto.video.MemberTestVideoResponseDto;
import com.danshow.danshowserver.web.dto.video.VideoMainResponseDto;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/*
서버 배포용 service
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class VideoServiceRelease implements VideoServiceInterface{

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final VideoPostRepository videoPostRepository;
    private final FileRepository fileRepository;
    private final S3Uploader s3Uploader;
    private final VideoFileUtils videoFileUtils;

    //단순 영상 저장용. 예를들어 분석이 필요없는 단순 강의영상.
    public Long save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId) throws Exception {

        User dancer = userRepository.findByEmail(userId);

        //uuid 붙인 파일명 생성
        UUID uuid = UUID.randomUUID();
        String uuidFileName = uuid+"-"+video.getOriginalFilename();

        AttachFile uploadedVideo = uploadFile(video,uuidFileName,"video");
        AttachFile uploadImage = uploadThumbnail(video, uuidFileName);

        String audioPath = uploadAudio(video,uuidFileName); //mp3 추출 후 업로드 로직

        VideoPost videoPost = VideoPost.createVideoPost(videoPostSaveDto, dancer, uploadedVideo,  uploadImage, audioPath);
        videoPostRepository.save(videoPost);
        return videoPost.getId();
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
                        .lastIndexOf("/")+1);
        
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
            // 최대 6개까지만 리스트에 담기d
            thumbnailList.add(makeThumbnail(all.get(i)));
        }
        return VideoMainResponseDto.builder()
                .videoThumbnailList(thumbnailList)
                .build();
    }

    public Thumbnail makeThumbnail(VideoPost videoPost) {
        return Thumbnail.builder()
                .videoPostId(videoPost.getId())
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
        Optional<VideoPost> vp = videoPostRepository.findById(id);
        return vp.orElse(null);
    }

    @Override
    public ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) {
        return null;
    }


    @TimeCheck
    public File uploadMemberTestVideo(MultipartFile memberVideo, Long id) throws IOException {
        final String localPath = System.getProperty("user.dir") + "/tmp";

        String memberVideoPath = localPath+"/"+memberVideo.getOriginalFilename();

        log.info("uploadMemberTestVideo: member-test video originalFileName : " + memberVideo.getOriginalFilename());
        log.info("uploadMemberTestVideo : member-test video created at : "+memberVideoPath);

        //1. 유저 비디오를 로컬에 저장한다.
        memberVideo.transferTo(new File(memberVideoPath));

        //2. 댄서 비디오를 가져와서 저장한다.
        AttachFile dancerVideo = fileRepository.findById(id).orElseThrow(() -> new NoSuchElementException("no video"));
        byte[] bytes = s3Uploader.getObject(dancerVideo);
        String temporalFilePath = System.getProperty("user.dir") + "/tmp/"+dancerVideo.getOriginalFileName();
        videoFileUtils.writeToFile(temporalFilePath, bytes);

        //3. 유저 비디오와 댄서 비디오를  합친 후 로컬에 저장한다.
        String integratedPath =
                videoFileUtils.integrateFileSideBySide(memberVideoPath, temporalFilePath,
                        localPath + "/integrated_" + memberVideo.getOriginalFilename());

        log.info("uploadMemberTestVideo : user and member video integrated");

        //4. 합쳐진 비디오를 s3에 업로드한다.
        String uploadedPath =
                s3Uploader.upload(integratedPath, dancerVideo.getOriginalFileName(), "video");

        AttachFile savedMemberTestVideo = AttachFile.builder()
                .filePath(uploadedPath)
                .originalFileName(memberVideo.getOriginalFilename())
                .filename("integrated_"+memberVideo.getOriginalFilename())
                .build();

        fileRepository.save(savedMemberTestVideo);

        //로컬의 파일들을 삭제한다.
        File memberFile = new File(memberVideoPath);
        memberFile.delete();

        File dancerFile = new File(temporalFilePath);
        dancerFile.delete();

        File integratedFile = new File(integratedPath);

        log.info("upload member finished. saved location : " + integratedFile.getAbsolutePath());

        return integratedFile;
    }

    public String uploadAudio(MultipartFile video, String customFileName) throws IOException {
        String audioPath = videoFileUtils.extractAudio(video,customFileName);
        String fileName = audioPath
                .substring(audioPath
                        .lastIndexOf("/")+1);
        System.out.println("fileName!! = " + fileName);
        //S3업로드 된 주소
        String filePath = s3Uploader.upload(audioPath,fileName,"audio");

        //오디오 업로드 후 임시파일 삭제
        File deleteFile = new File(audioPath);

        if(deleteFile.exists()) {
            deleteFile.delete();
        }

        return filePath;
    }

    public List<MemberTestVideoResponseDto> getMemberTestVideoList(String email) {
        Member member = memberRepository.findByEmail(email);
        List<MemberTestVideoPost> memberTestVideoList = member.getMemberTestVideoList();
        //각 MemberTestVideoPost를 Dto로 변환해서 리스트를 반환
        return memberTestVideoList.stream().map(s -> memberTestVideoPostToDto(s)).collect(Collectors.toList());
    }

    private MemberTestVideoResponseDto memberTestVideoPostToDto(MemberTestVideoPost post) {
        Long id = post.getId();
        VideoPost videoPost = post.getVideoPost();
        return MemberTestVideoResponseDto.builder()
                .title(videoPost.getTitle())
                .filePath(videoPost.getVideo().getFilePath())
                .score(post.getScore())
                .build();
    }

    public String getMusicUrl(Long id) {
        Optional<VideoPost> video = videoPostRepository.findById(id);
        if(video.isPresent()) {
            return video.get().getMusicPath();
        }
        return "";
    }
}
