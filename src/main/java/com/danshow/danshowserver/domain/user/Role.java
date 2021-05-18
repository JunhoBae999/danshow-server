package com.danshow.danshowserver.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    MEMBER("ROLE_MEMBER","멤버십 사용자"),
    DANCER("ROLE_DANCER", "댄서"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
