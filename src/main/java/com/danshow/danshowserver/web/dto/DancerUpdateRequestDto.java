package com.danshow.danshowserver.web.dto;

import com.danshow.danshowserver.domain.user.Dancer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DancerUpdateRequestDto {
    private String email;
    private String dancer_description;

    @Builder
    public DancerUpdateRequestDto(String email, String dancer_description) {
        this.email = email;
        this.dancer_description = dancer_description;
    }

    public Dancer toEntity(Dancer dancer) {
        if(dancer_description!=null)
            dancer.setDancer_description(dancer_description);
        return dancer;
    }
}
