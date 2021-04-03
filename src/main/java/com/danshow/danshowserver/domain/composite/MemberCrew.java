package com.danshow.danshowserver.domain.composite;

import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.user.Member;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "Member_crew")
@IdClass(MemberCrewId.class)
@Entity
public class MemberCrew {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;
}
