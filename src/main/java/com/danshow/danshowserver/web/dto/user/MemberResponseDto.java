package com.danshow.danshowserver.web.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

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

}
