package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private VideoPost videoPost;


    public void setVideoPost(VideoPost videoPost) {
        this.videoPost = videoPost;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
