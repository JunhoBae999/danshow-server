package com.danshow.danshowserver.domain.comment;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.video.CoverVideo;
import com.danshow.danshowserver.domain.video.Video;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="comment_id")
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Member member;
    /*멤버 쪽에선 자기가 단 코멘트 조회 안해도 될거 같아 단방향으로 했습니다*/

    @ManyToOne
    @JoinColumn(name = "video_id")
    private CoverVideo coverVideo;

    public void setComment(CoverVideo coverVideo) {
        this.coverVideo = coverVideo;
        //무한루프 방지
        if(!coverVideo.getCommentList().contains(this)) {
            coverVideo.getCommentList().add(this);
        }
    }
}
