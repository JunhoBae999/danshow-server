package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AttachFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="file_id")
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    protected VideoPost videoPost;

    public void setVideoPost(VideoPost videoPost) {
        this.videoPost = videoPost;
        videoPost.setVideo(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

}
