package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Builder
@DiscriminatorColumn(name = "type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Video extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="video_id")
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "dancer_id")
    private Dancer dancer;
    //TODO : USER를 참조하면 안될까

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="video_post_id")
    private VideoPost videoPost;

    public void setVideoPost(VideoPost videoPost) {
        this.videoPost = videoPost;
    }

    public void setDancer(Dancer dancer) {
        this.dancer = dancer;
    }
}
