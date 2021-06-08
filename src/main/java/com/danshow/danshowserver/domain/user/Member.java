package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.composite.MemberCrew;
import com.danshow.danshowserver.domain.video.post.MemberTestVideoPost;
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
    public Member(String email, String nickname, String name, Role role, String password, String salt,
                  Boolean membership, String profile_description, String profile_picture){
        super(email,nickname,name, role,password,salt);
        this.membership = membership;
        this.profile_description = profile_description;
        this.profile_picture = profile_picture;
    }

    @OneToMany(mappedBy = "member")
    private List<MemberTestVideoPost> memberTestVideoList = new ArrayList<MemberTestVideoPost>();

    @OneToMany(mappedBy = "member")
    private List<MemberCrew> crewList = new ArrayList<MemberCrew>();

    public void addVideo (MemberTestVideoPost memberTestVideo) {
        this.memberTestVideoList.add(memberTestVideo);
        if(memberTestVideo.getUser() != this) {
            memberTestVideo.setUser(this);
        }
    }

    public void addCrew (MemberCrew crew) {
        this.crewList.add(crew);
        if(crew.getMember() != this) {
            crew.setMember(this);
        }
    }


}
