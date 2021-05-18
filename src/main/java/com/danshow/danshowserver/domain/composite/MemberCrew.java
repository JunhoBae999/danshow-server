package com.danshow.danshowserver.domain.composite;

import com.danshow.danshowserver.domain.crew.Crew;
import com.danshow.danshowserver.domain.user.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Table(name = "Member_crew")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_crew_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    public void setMember(Member member) {
        this.member = member;
        if(!member.getCrewList().contains(this)) {
            member.getCrewList().add(this);
        }
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
        if(!crew.getMemberList().contains(this)) {
            crew.getMemberList().add(this);
        }
    }
}
