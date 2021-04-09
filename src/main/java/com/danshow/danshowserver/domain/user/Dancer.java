package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.video.Video;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("Dancer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dancer extends User {

    private Boolean membership;
    private String dancer_description;
    private String dancer_picture;

    @OneToMany(mappedBy = "dancer")
    private List<Video> videoList = new ArrayList<Video>();

    public void addVideo (Video video) {
        this.videoList.add(video);
        //무한루프 방지
        if(video.getDancer() != this) {
            video.setDancer(this);
        }
    }

    @OneToOne(mappedBy = "dancer")
    private Crew crew;

    @Builder
    public Dancer(String email, String nickname, String name,
                  Boolean membership, String dancer_description, String dancer_picture){
        super(email,nickname,name);
        this.membership = membership;
        this.dancer_description = dancer_description;
        this.dancer_picture = dancer_picture;
    }
}