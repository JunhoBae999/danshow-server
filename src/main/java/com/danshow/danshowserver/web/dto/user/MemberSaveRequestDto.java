package com.danshow.danshowserver.web.dto.user;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.Role;
import com.danshow.danshowserver.service.user_service.SHA256Util;
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
        String salt = SHA256Util.generateSalt();
        String encryptedPassword = SHA256Util.getEncrypt(password,salt);
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .password(encryptedPassword)
                .salt(salt)
                .name(name)
                .membership(membership)
                .role(Role.MEMBER)
                .profile_description(profile_description)
                .profile_picture(profile_picture)
                .build();
    }
}
