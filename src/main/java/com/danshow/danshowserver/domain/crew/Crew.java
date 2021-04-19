package com.danshow.danshowserver.domain.crew;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.composite.MemberCrew;
import com.danshow.danshowserver.domain.user.Dancer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "crew")
    private List<MemberCrew> memberCrewList;

}
