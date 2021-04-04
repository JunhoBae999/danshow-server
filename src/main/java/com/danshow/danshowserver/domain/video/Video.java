package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="video_id")
    private Long id;

    private String title;
    private Long difficulty;
    private String video_address;
    private String gender;
    private String directory;
    private String genre;
    private Long click;
    private Long length;
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Dancer dancer;

    public void setDancer(Dancer dancer) {
        this.dancer = dancer;
        //무한루프 방지
        if(!dancer.getVideoList().contains(this)) {
            dancer.getVideoList().add(this);
        }
    }
}
