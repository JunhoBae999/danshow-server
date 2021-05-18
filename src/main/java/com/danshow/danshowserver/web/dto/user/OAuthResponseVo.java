package com.danshow.danshowserver.web.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class OAuthResponseVo {

    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private String email_verified;
    private String locale;
}
