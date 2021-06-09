package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.video.post.CoverVideoPost;
import com.danshow.danshowserver.domain.video.post.LectureVideoPost;
import com.danshow.danshowserver.domain.video.post.MemberTestVideoPost;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("Dancer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dancer extends User {

    private String dancer_description;
    private String dancer_picture;

    @OneToOne(mappedBy = "dancer")
    private Crew crew;

    @OneToMany(mappedBy = "user")
    private List<LectureVideoPost> lectureVideoList = new ArrayList<LectureVideoPost>();

    public void addLecturVideoPost (LectureVideoPost videoPost) {
        this.lectureVideoList.add(videoPost);
        //무한루프 방지
        if(videoPost.getUser() != this) {
            videoPost.setUser(this);
        }
    }

    @OneToMany(mappedBy = "user")
    private List<CoverVideoPost> coverVideoList = new ArrayList<CoverVideoPost>();

    public void addCoverVideoPost (CoverVideoPost videoPost) {
        this.coverVideoList.add(videoPost);
        //무한루프 방지
        if(videoPost.getUser() != this) {
            videoPost.setUser(this);
        }
    }



    @Builder
    public Dancer(String email, String nickname, String name, Role role, String password, String salt,
                  String dancer_description, String dancer_picture){
        super(email,nickname,name, role,password,salt);
        this.dancer_description = dancer_description;
        this.dancer_picture = dancer_picture;
    }
}