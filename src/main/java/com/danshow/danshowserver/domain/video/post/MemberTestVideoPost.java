package com.danshow.danshowserver.domain.video.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Table(name = "member_test_video_post")
@Entity
@DiscriminatorValue("member_test")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MemberTestVideoPost extends VideoPost{
}
