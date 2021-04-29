package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.domain.crew.Crew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"3.Crew"})
@RestController
@RequiredArgsConstructor
public class CrewController {

    @ApiOperation(value = "Create Crew", notes = "Create Crew by Dancer")
    @PostMapping("/api/v1/create_crew")
    public void createCrew() {
    }

    @ApiOperation(value = "delete Crew", notes = "delete Crew by Dancer")
    @PostMapping("/api/v1/{crew_id}/delete")
    public void deleteCrew(@PathVariable("crew_id") Long id) {
    }

    @ApiOperation(value = "Join Crew", notes = "Member can join the Crew of Dancer")
    @GetMapping("/api/v1/{crew_name}/join")
    public Crew joinCrew() {
        return null;
    }

    @ApiOperation(value = "Get All available crews", notes = "Member can get all crews with are available")
    @GetMapping("/api/v1/crews")
    public Crew getCrewList() {
        return null;
    }



}
