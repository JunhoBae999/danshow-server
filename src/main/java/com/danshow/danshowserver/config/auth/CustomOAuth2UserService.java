package com.danshow.danshowserver.config.auth;

import com.danshow.danshowserver.config.auth.dto.OAuthAttributes;
import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.MemberRepository;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        OAuthAttributes attributes = new OAuthAttributes(oAuth2User.getAttributes());

        /*TODO 프린트 빼기*/
        System.out.println("attributes = " + attributes);

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user",new SessionUser(user));

        /*권한 설정 아직 안했음*/
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes.getAttributes(),
                "sub"); //구글만 할거라서 sub로 고정
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        /*TODO 업데이트 구현하기*/
        User user = userRepository.findByEmail(attributes.getEmail());

        if(user == null) {
            return memberRepository.save(attributes.toEntity()); //유저 정보 없으면 "멤버"에 저장
        }
        return user;

    }

}
