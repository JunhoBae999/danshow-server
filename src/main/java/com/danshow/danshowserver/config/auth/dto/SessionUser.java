package com.danshow.danshowserver.config.auth.dto;

import com.danshow.danshowserver.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

//TODO 이제 안씀
@Setter
@Getter
@NoArgsConstructor
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
