package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VideoPost {

    @Id @GeneratedValue
    @Column(name="video_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "videoPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private Video video;

    private String title;

    private String description;

    private Long difficulty;

    private String gender;

    private String genre;

    private Long click = 0L;

    private Long length;

    private String type;


    //연관관계 메서드
    public void addFile(Video video) {
        video.setVideoPost(this);
        this.video = video;
    }

    public void increaseClick() {
        this.click += 1L;
    }

    //Post 생성을 위한
    public static VideoPost createVideoPost(VideoPostSaveDto videoPostSaveDto, User user, Video requestVideo) {
        VideoPost videoPost = new VideoPost();

        videoPost.description = videoPostSaveDto.getDescription();
        videoPost.title = videoPostSaveDto.getTitle();
        videoPost.user = user;
        videoPost.gender = videoPostSaveDto.getGender();
        videoPost.genre = videoPostSaveDto.getGenre();
        videoPost.length = videoPostSaveDto.getLength();
        videoPost.difficulty = videoPost.getDifficulty();
        videoPost.type = videoPostSaveDto.getType();
        videoPost.video = requestVideo;
        requestVideo.setVideoPost(videoPost);

        return videoPost;

    }








}
