package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.composite.MemberCrew;
import com.danshow.danshowserver.domain.composite.MemberVideo;
import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.video.MemberTestVideo;
import com.danshow.danshowserver.domain.video.Video;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends User {

    private Boolean membership;
    private String profile_description;
    private String profile_picture;

    @Builder
    public Member(String email, String nickname, String name,
                  Boolean membership, String profile_description, String profile_picture){
        super(email,nickname,name);
        this.membership = membership;
        this.profile_description = profile_description;
        this.profile_picture = profile_picture;
    }

    @OneToMany(mappedBy = "member")
    private List<MemberTestVideo> memberTestVideoList = new ArrayList<MemberTestVideo>();


    public void addVideo (MemberTestVideo memberTestVideo) {
        this.memberTestVideoList.add(memberTestVideo);
        //무한루프 방지
        if(memberTestVideo.getMember() != this) {
            memberTestVideo.setMember(this);
        }
    }

    @OneToMany(mappedBy = "member")
    private List<MemberCrew> memberCrewList;


    @OneToMany(mappedBy = "member")
    private List<MemberVideo> memberVideoList;
}
