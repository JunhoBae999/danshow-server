package com.danshow.danshowserver.domain.video.post;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Table(name = "lecture_video_post")
@Entity
@DiscriminatorValue("lecture")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class LectureVideoPost extends VideoPost{

    public LectureVideoPost(VideoPost videoPost) {
        super(videoPost);
    }
}
