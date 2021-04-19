package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.user.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequestDto {
    private String email;
    private String nickname;
    private Boolean membership;
    private String profile_description;
    //private String profile_picture;

    @Builder
    public MemberUpdateRequestDto(String email, String nickname, Boolean membership,
                                  String profile_description) {
        this.email = email;
        this.nickname = nickname;
        this.membership = membership;
        this.profile_description = profile_description;
    }

    public Member toEntity(Member member) {
        if(membership!=null)
            member.setMembership(membership);
        if(nickname!=null)
            member.setNickname(nickname);
        if(profile_description!=null)
            member.setProfile_description(profile_description);
        return member;
    }

}
