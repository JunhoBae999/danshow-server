package com.danshow.danshowserver.domain.video.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {

    COVER("TYPE_COVER","커버 댄스 영상"),
    LECTURE("TYPE_LECTURE", "강의 영상"),
    TEST("TYPE_TEST", "멤버 테스트 영상");

    private final String key;
    private final String title;
}
