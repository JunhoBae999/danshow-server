package com.danshow.danshowserver.domain.crew;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import com.danshow.danshowserver.domain.user.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
public class Crew extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Long id;

    private String description;
    private String crew_profile_image;

    @OneToOne
    @JoinColumn(name ="user_id")
    private Dancer dancer;

    @ManyToMany(mappedBy = "crewList")
    private List<Member> memberList;
}
