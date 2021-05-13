package com.danshow.danshowserver.config.auth.dto;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.Role;
import lombok.Getter;

import java.util.Map;

//TODO 이제 안씀
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String picture;

    public OAuthAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.name = (String) attributes.get("name");
        this.email = (String) attributes.get("email");
        this.picture = (String) attributes.get("picture");
    }

    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .nickname("nickname")
                .name(this.name)
                .role(Role.GUEST)
                .membership(Boolean.FALSE)
                .profile_description("No description")
                .profile_picture(this.picture)
                .build();

    }
}
