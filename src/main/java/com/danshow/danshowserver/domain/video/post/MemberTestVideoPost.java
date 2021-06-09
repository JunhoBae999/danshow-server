package com.danshow.danshowserver.domain.video.post;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    private Long score;

    public MemberTestVideoPost(VideoPostSaveDto videoPostSaveDto, Member member,
                               AttachFile uploadedVideo, AttachFile uploadImage, String audioPath) {

        super.setData(videoPostSaveDto, uploadedVideo,  uploadImage, audioPath);
        setUser(member);
        this.score = videoPostSaveDto.getScore();

    }

    public void setUser(Member member) {
        this.member = member;
        if(!member.getMemberTestVideoPostList().contains(this)) {
            member.getMemberTestVideoPostList().add(this);
        }
    }
}
