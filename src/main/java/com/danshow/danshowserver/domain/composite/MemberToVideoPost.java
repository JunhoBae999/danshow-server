package com.danshow.danshowserver.domain.composite;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.video.Video;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Member_VideoPost")
@IdClass(MemberVideoId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberToVideoPost {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "video_post_id")
    private VideoPost videoPost;

    private boolean favorite;
    private boolean passed;

}
