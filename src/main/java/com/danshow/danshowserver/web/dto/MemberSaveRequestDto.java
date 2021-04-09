package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.user.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSaveRequestDto {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private Boolean membership;
    private String profile_description;
    private String profile_picture;

    @Builder
    public MemberSaveRequestDto(String email, String password, String nickname, String name,
                                Boolean membership, String profile_description, String profile_picture) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.membership = membership;
        this.profile_description = profile_description;
        this.profile_picture = profile_picture;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .name(name)
                .membership(membership)
                .profile_description(profile_description)
                .profile_picture(profile_picture)
                .build();
    }
}
