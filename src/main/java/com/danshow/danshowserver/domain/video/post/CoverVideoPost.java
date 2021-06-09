package com.danshow.danshowserver.domain.video.post;

import com.danshow.danshowserver.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @OneToMany(mappedBy = "coverVideoPost")
    private List<Comment> commentList = new ArrayList<Comment>();

    public void addComment (Comment comment) {
        this.commentList.add(comment);
        //무한루프 방지
        if(comment.getCoverVideoPost() != this) {
            comment.setCoverVideoPost(this);
        }
    }

    public CoverVideoPost(VideoPost videoPost) {
        super(videoPost);
    }

}
