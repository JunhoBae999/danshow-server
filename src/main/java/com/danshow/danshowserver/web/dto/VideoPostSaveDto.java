package com.danshow.danshowserver.web.dto;

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

}
