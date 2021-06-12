package com.danshow.danshowserver.domain.video.post;


import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Table(name = "lecture_video_post")
@Entity
@DiscriminatorValue("lecture")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class LectureVideoPost extends VideoPost{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Dancer dancer;

    public LectureVideoPost(VideoPostSaveDto videoPostSaveDto, Dancer dancer,
                            AttachFile uploadedVideo, AttachFile uploadImage, String audioPath) {
        super.setData(videoPostSaveDto, uploadedVideo,  uploadImage, audioPath);
        setUser(dancer);
    }

    public void setUser(Dancer dancer) {
        this.dancer = dancer;
        if(!dancer.getLectureVideoList().contains(this)) {
            dancer.getLectureVideoList().add(this);
        }
    }
}
