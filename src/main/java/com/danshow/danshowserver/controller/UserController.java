package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.user.User;
import com.danshow.danshowserver.service.DancerService;
import com.danshow.danshowserver.service.MemberService;
import com.danshow.danshowserver.web.dto.user.DancerUpdateRequestDto;
import com.danshow.danshowserver.web.dto.user.MemberResponseDto;
import com.danshow.danshowserver.web.dto.user.MemberUpdateRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1.User"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;
    private final DancerService dancerService;

    @ApiOperation(value = "멤버 업데이트" , notes = "멤버의 정보를 업데이트 합니다.")
    @PostMapping("user/member-update")
    public ResponseEntity<String> memberUpdate(@RequestBody MemberUpdateRequestDto updateRequestDto) {
        memberService.update(updateRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "멤버 전환",notes = "멤버를 안무가로 전환합니다.")
    @PostMapping("user/member-to-dancer")
    public ResponseEntity<String> memberToDancer(@RequestParam String email) {
        Long result = memberService.toDancer(email);
        if(result>=0)
            return new ResponseEntity<>("success", HttpStatus.OK);
        return new ResponseEntity<>("already dancer", HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "안무가 업데이트",notes = "안무가의 정보를 업데이트합니다.")
    @PostMapping("user/dancer-update")
    public ResponseEntity<String> dancerUpdate(@RequestBody DancerUpdateRequestDto updateRequestDto) {
        dancerService.update(updateRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "유저 정보 조회", notes = "유저의 정보를 조회합니다.")
    @GetMapping("user/info/{id}") //TODO 이메일로 조회할지?
    public ResponseEntity<MemberResponseDto> getUserInfo(@ApiParam(value = "유저 식별")@PathVariable Long id) {
        return new ResponseEntity<>(memberService.findById(id),HttpStatus.OK);
    }
}
