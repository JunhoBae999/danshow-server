package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.crew.Crew;
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

    @OneToMany(mappedBy = "user")
    private List<VideoPost> videoList = new ArrayList<VideoPost>();

    public void addVideoPost (VideoPost videoPost) {
        this.videoList.add(videoPost);
        //무한루프 방지
        if(videoPost.getUser() != this) {
            //TODO : 테스트 필
            videoPost.setUser(this);
        }
    }

    @OneToOne(mappedBy = "dancer")
    private Crew crew;

    @Builder
    public Dancer(String email, String nickname, String name,
                  String dancer_description, String dancer_picture){
        super(email,nickname,name);
        this.dancer_description = dancer_description;
        this.dancer_picture = dancer_picture;
    }
}