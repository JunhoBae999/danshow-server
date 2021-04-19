package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.video.post.VideoPost;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoPostResponseDto {

    private Long id;

    private Long userId;

    private String filePath;

    private Long difficulty;

    private String gender;

    private String genre;

    private Long click;

    private Long length;

    private String songName;

    private String title;

    private String type;

    private ImageResponseDto thumbnail;

    private VideoResponseDto videoResponseDto;

    public static VideoPostResponseDto createVideoPostResponseDto(VideoPost videoPost) {

        VideoPostResponseDto videoPostResponseDto = new VideoPostResponseDto();

        videoPostResponseDto.id = videoPost.getId();
        videoPostResponseDto.userId = videoPost.getUser().getId();
        videoPostResponseDto.filePath = videoPost.getVideo().getFilePath();
        videoPostResponseDto.difficulty = videoPost.getDifficulty();
        videoPostResponseDto.gender = videoPost.getGender();
        videoPostResponseDto.click = videoPost.getClick();
        videoPostResponseDto.length = videoPost.getLength();
        videoPostResponseDto.songName = videoPost.getSongName();
        videoPostResponseDto.title = videoPost.getTitle();
        videoPostResponseDto.type = videoPost.getType();
        videoPostResponseDto.thumbnail = new ImageResponseDto(videoPost.getImage());
        videoPostResponseDto.videoResponseDto = new VideoResponseDto(videoPost.getVideo());

        return videoPostResponseDto;
    }


}
