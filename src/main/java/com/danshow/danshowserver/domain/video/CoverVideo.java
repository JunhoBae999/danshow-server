package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.comment.Comment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Table(name = "Cover_Video")
@Entity
@DiscriminatorValue("cover")
public class CoverVideo extends Video{

    private String song_name;

    @OneToMany(mappedBy = "coverVideo")
    private List<Comment> commentList = new ArrayList<Comment>();

    public void addComment (Comment comment) {
        this.commentList.add(comment);
        //무한루프 방지
        if(comment.getCoverVideo() != this) {
            comment.setCoverVideo(this);
        }
    }
}
