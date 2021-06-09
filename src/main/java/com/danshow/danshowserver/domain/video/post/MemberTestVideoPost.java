package com.danshow.danshowserver.domain.video.post;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Table(name = "member_test_video_post")
@Entity
@DiscriminatorValue("member_test")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MemberTestVideoPost extends VideoPost{

    private Long score;

    public MemberTestVideoPost(VideoPost videoPost, Long score) {
        super(videoPost);
        this.score = score;
    }

    public void setUser(Member member) {
        this.setUser(member);
        if(!member.getMemberTestVideoPostList().contains(this)) {
            member.getMemberTestVideoPostList().add(this);
        }
    }
}
