package com.danshow.danshowserver.domain.composite;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class MemberVideoId implements Serializable {

    private String member;
    private String videoPost;

    public MemberVideoId(String member, String videoPost) {
        this.member = member;
        this.videoPost = videoPost;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        MemberVideoId memberVideoId = (MemberVideoId) obj;
        return (this.getMember().equals(memberVideoId.getMember()) && this.getVideoPost().equals(memberVideoId.getVideoPost()));
    }

    @Override
    public int hashCode() {
        int h = 0;
        String concat = member.concat(getVideoPost());
        if(concat.length() > 0) {
            for(char ch : concat.toCharArray()) {
                h = 31 * h + ch;
            }
        }
        return h;
    }
}
