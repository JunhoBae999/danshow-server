package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.service.DancerService;
import com.danshow.danshowserver.service.MemberService;
import com.danshow.danshowserver.web.dto.DancerUpdateRequestDto;
import com.danshow.danshowserver.web.dto.MemberUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;
    private final DancerService dancerService;

    @PostMapping("user/member-update")
    public ResponseEntity memberUpdate(@RequestBody MemberUpdateRequestDto updateRequestDto) {
        memberService.update(updateRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("user/member-to-dancer")
    public ResponseEntity memberToDancer(@RequestParam String email) {
        Long result = memberService.toDancer(email);
        if(result>=0)
            return new ResponseEntity<>("success", HttpStatus.OK);
        return new ResponseEntity<>("already dancer", HttpStatus.ACCEPTED);
    }

    @PostMapping("user/dancer-update")
    public ResponseEntity dancerUpdate(@RequestBody DancerUpdateRequestDto updateRequestDto) {
        dancerService.update(updateRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
