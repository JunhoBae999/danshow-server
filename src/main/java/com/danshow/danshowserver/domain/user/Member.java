package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.composite.MemberCrew;
import com.danshow.danshowserver.domain.composite.MemberToVideoPost;
import com.danshow.danshowserver.domain.video.post.MemberTestVideoPost;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
//@DiscriminatorValue("Member")
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

    @OneToMany(mappedBy = "user")
    private List<MemberTestVideoPost> memberTestVideoList = new ArrayList<MemberTestVideoPost>();


    public void addVideo (MemberTestVideoPost memberTestVideo) {
        this.memberTestVideoList.add(memberTestVideo);
        //무한루프 방지
        if(memberTestVideo.getUser() != this) {
            memberTestVideo.setUser(this);
        }
    }

    @OneToMany(mappedBy = "member")
    private List<MemberCrew> memberCrewList;

    @OneToMany(mappedBy = "member")
    private List<MemberToVideoPost> memberVideoList;
}
