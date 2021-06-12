package com.danshow.danshowserver.domain.video.post;

import com.danshow.danshowserver.domain.comment.Comment;
import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Table(name = "cover_Video_Post")
@Entity
@DiscriminatorValue("cover")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class CoverVideoPost extends VideoPost{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Dancer dancer;

    @OneToMany(mappedBy = "coverVideoPost")
    private List<Comment> commentList = new ArrayList<Comment>();

    public CoverVideoPost(VideoPostSaveDto videoPostSaveDto, Dancer dancer,
                          AttachFile uploadedVideo, AttachFile uploadImage, String audioPath) {
        super.setData(videoPostSaveDto, uploadedVideo,  uploadImage, audioPath);
        setUser(dancer);
    }

    public void addComment (Comment comment) {
        this.commentList.add(comment);
        //무한루프 방지
        if(comment.getCoverVideoPost() != this) {
            comment.setCoverVideoPost(this);
        }
    }

    public void setUser(Dancer dancer) {
        this.dancer = dancer;
        if(!dancer.getCoverVideoList().contains(this)) {
            dancer.getCoverVideoList().add(this);
        }
    }

}
