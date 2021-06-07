package com.danshow.danshowserver.service.user_service;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.config.auth.dto.SessionUser;
import com.danshow.danshowserver.domain.user.*;
import com.danshow.danshowserver.web.dto.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final DancerRepository dancerRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public String login(String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://openidconnect.googleapis.com/v1/userinfo?access_token=";
        OAuthResponseVo userInfo;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(access_token, Map.class);
            userInfo =  restTemplate.getForObject(url+map.get("access_token"),OAuthResponseVo.class);
        } catch (Exception e) { //유저정보 획득 실패
            log.warn("Wrong access token: {}",e.toString());
            return "";
        }

        //유저정보 획득 성공시
        String email = userInfo.getEmail();
        User user = userRepository.findByEmail(email);
        String role;
        if(user == null) { //최초 로그인 = 회원 등록
            String name = userInfo.getName();
            String picture = userInfo.getPicture();
            memberRepository.save(Member.builder()
                    .email(email)
                    .nickname("nickname")
                    .name(name)
                    .role(Role.GUEST)
                    .membership(Boolean.FALSE)
                    .profile_description("No description")
                    .profile_picture(picture)
                    .build());
            role = Role.GUEST.getKey();
        } else {
            role = user.getRoleKey();
        }
        return tokenProvider.createToken(email,role);
    }

    @Transactional
    public String login(String email, String password) {
        if(checkAccount(email,password)) {
            User user = userRepository.findByEmail(email);
            return tokenProvider.createToken(email,user.getRoleKey());
        }
        return "";
    }

    public String login(LoginDto loginDto) {
        return login(loginDto.getEmail(),loginDto.getPassword());
    }

    @Transactional
    public Long save(MemberSaveRequestDto requestDto) {
        Member member = requestDto.toEntity();
        if(memberRepository.findByEmail(member.getEmail())!=null) {
            /* 이메일이 중복 될 경우 */
            return -1L;
        }
        return memberRepository.save(member).getId();
    }

    @Transactional
    public Long update(MemberUpdateRequestDto requestDto, String email) {
        Member member = memberRepository.findByEmail(email);
        member = requestDto.toEntity(member); //update
        return member.getId();
    }

    @Transactional
    public Long toDancer(String email) {
        Member member;
        try {
            member = memberRepository.findByEmail(email);
            Dancer dancer = Dancer.builder()
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .name(member.getName())
                    .role(Role.DANCER)
                    .dancer_description(member.getProfile_description())
                    .dancer_picture(member.getProfile_picture())
                    .build();
            dancerRepository.save(dancer);
            memberRepository.delete(member);
            return dancer.getId();
        } catch (Exception e){
            return -1L;
        }

    }

    @Transactional
    public MemberResponseDto findById(Long id) {
        User user = (User) userRepository.findById(id).orElse(null);
        if(user == null) {
            return MemberResponseDto.builder().build();
        } else if(user instanceof Member) {
            Member member = (Member) memberRepository.findById(id).orElse(null);
            return MemberResponseDto.builder()
                    .email(member.getEmail())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .membership(member.getMembership())
                    .profile_description(member.getProfile_description())
                    .profile_picture(member.getProfile_picture())
                    .build();
        } else {
            Dancer dancer = (Dancer) dancerRepository.findById(id).orElse(null);
            return MemberResponseDto.builder()
                    .email(dancer.getEmail())
                    .name(dancer.getName())
                    .nickname(dancer.getNickname())
                    .profile_description(dancer.getDancer_description())
                    .profile_picture(dancer.getDancer_picture())
                    .build();
        }
    }

    private boolean checkAccount(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user==null)
            return false;
        String encryptedPassword = user.getPassword();
        String salt = user.getSalt();
        return SHA256Util.getEncrypt(password,salt).equals(encryptedPassword);
    }

}
