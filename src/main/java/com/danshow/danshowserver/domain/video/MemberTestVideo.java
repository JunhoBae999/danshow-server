package com.danshow.danshowserver.domain.video;


import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.Member;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "member_test_video")
@Entity
@DiscriminatorValue("member_test")
public class MemberTestVideo extends Video{

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        //무한루프 방지
        if(!member.getMemberTestVideoList().contains(this)) {
            member.getMemberTestVideoList().add(this);
        }
    }
}
