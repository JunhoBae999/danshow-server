package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.video.post.PostType;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoPostSaveDto {

    private String title;

    private String description;

    private String userId; //TODO: 삭제될 가능성 있음. Principal로 받는 등.

    private Long difficulty;

    private String genre;

    private String gender;

    private Long length;

    private Long score;

    private PostType postType;

    public static VideoPostSaveDto of(VideoPost videoPost, User user) {

        //멤버테스트 비디오와의
        return VideoPostSaveDto.builder()
                .title(videoPost.getTitle())
                .description(videoPost.getDescription())
                .userId(user.getEmail())
                .difficulty(videoPost.getDifficulty())
                .gender(videoPost.getGender())
                .genre(videoPost.getGenre())
                .length(videoPost.getLength())
                .postType(PostType.TEST)
                .build();
    }
}
