package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.user.Role;
import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.service.DancerService;
import com.danshow.danshowserver.service.MemberService;
import com.danshow.danshowserver.web.dto.user.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = {"1.User"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;
    private final DancerService dancerService;
    private final TokenProvider tokenProvider;

    @ApiOperation(value = "로그인" , notes = "구글api 액세스 토큰으로 로그인(회원가입) 합니다")
    @PostMapping("user/login")
    public ResponseEntity<String> login(@ApiParam(value = "액세스 토큰", required = true) @RequestBody String access_token) {
        String Jwt = memberService.login(access_token);
        return new ResponseEntity<>(Jwt, HttpStatus.OK); //TODO 액세스 토큰이 유효하지 않을 때 리스폰을 어떻게할까
    }


    @ApiOperation(value = "Update Member" , notes = "Member can update the profile")
    @PostMapping("user/member-update")
    public ResponseEntity<String> memberUpdate(@ApiParam(value = "수정 할 정보", required = false) @RequestBody MemberUpdateRequestDto updateRequestDto,
                                               @ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        memberService.update(updateRequestDto, email);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "To Dancer",notes = "Change the type of user - member to dancer")
    @PostMapping("user/member-to-dancer")
    public ResponseEntity<String> memberToDancer(@ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        Long result = memberService.toDancer(email);
        if(result>=0)
            return new ResponseEntity<>("success", HttpStatus.OK);
        return new ResponseEntity<>("already dancer", HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "Update Dancer",notes = "Update the profile of dancer")
    @PostMapping("user/dancer-update")
    public ResponseEntity<String> dancerUpdate(@RequestBody DancerUpdateRequestDto updateRequestDto,
                                               @ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        dancerService.update(updateRequestDto, email);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "유저 정보 조회", notes = "유저의 정보를 조회합니다.")
    @GetMapping("user/info/{id}") //TODO 이메일로 조회할지?
    public ResponseEntity<MemberResponseDto> getUserInfo(@ApiParam(value = "유저 식별")@PathVariable Long id) {
        return new ResponseEntity<>(memberService.findById(id),HttpStatus.OK);
    }

    @ApiOperation(value = "임시 JWT 발급", notes = "넘겨 준 정보로 가입하고 Jwt를 발급합니다.")
    @PostMapping("/jwt") //TODO 임시 엔드포인트
    public ResponseEntity<String> freeJwt(@RequestBody MemberSaveRequestDto requestDto) {

        try {
            memberService.save(requestDto);
        } catch (Exception e) {
            return new ResponseEntity<>("Need correct information",HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(tokenProvider.createToken(requestDto.getEmail(), Role.MEMBER.getKey()),HttpStatus.OK);
    }
}
