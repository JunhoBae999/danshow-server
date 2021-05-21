package com.danshow.danshowserver.web.dto.user;

import com.danshow.danshowserver.web.dto.Thumbnail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Getter
@Builder
public class MemberResponseDto {

    private String email;
    private String nickname;
    private String name;
    private String type;
    private Boolean membership;
    private String profile_description;
    private String profile_picture;
    private List<Thumbnail> recentVideoList;
    private List<Thumbnail> favoriteVideoList;
    private List<Thumbnail> myCrewList;
}
